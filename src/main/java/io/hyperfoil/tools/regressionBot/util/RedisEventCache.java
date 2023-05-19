package io.hyperfoil.tools.regressionBot.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.quarkus.arc.Arc;
import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheManager;
import io.quarkus.cache.CacheResult;
import io.quarkus.cache.redis.runtime.RedisCache;
import io.quarkus.redis.datasource.RedisDataSource;
import io.vertx.core.json.JsonObject;
import org.kohsuke.github.GHEventPayload;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHUser;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class RedisEventCache implements EventCache {

    private static final String cacheName = "perf-bot-event-cache";
    private final RedisCache redisCache;
    private final RedisDataSource redisDataSource;

    private final ObjectMapper customerMapper;

    public RedisEventCache() {
        CacheManager cacheManager = Arc.container().select(CacheManager.class).get();
        redisDataSource = Arc.container().select(RedisDataSource.class).get();

        Optional<Cache> cacheOpt = cacheManager.getCache(RedisEventCache.cacheName);

        Cache cache = cacheOpt.get();
        assert (cache instanceof RedisCache);
        redisCache = (RedisCache) cache;

        customerMapper = new ObjectMapper();

        SimpleModule module = new SimpleModule();
        module.addSerializer(GHEventPayload.IssueComment.class, new GHEventPayloadIssueCommentSerializer());
//        module.addSerializer(GHUser.class, new GHUserSerializer());
//        module.addSerializer(GHIssue.class, new GHIssueSerializer());
        customerMapper.registerModule(module);
        customerMapper.registerModule(new SimpleModule().setSerializerModifier(new BeanSerializerModifier() {
            @Override
            public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
                return beanProperties.stream().map(bpw -> new BeanPropertyWriter(bpw) {
                    @Override
                    public void serializeAsField(Object bean, JsonGenerator gen, SerializerProvider prov) throws Exception {
                        try {
                            super.serializeAsField(bean, gen, prov);
                        } catch (Exception e) {
                            System.out.println(String.format("ignoring %s for field '%s' of %s instance", e.getClass().getName(), this.getName(), bean.getClass().getName()));
                        }
                    }
                }).collect(Collectors.toList());
            }
        }));


//        disableObjectMapperIOExceptions();

    }

    public class GHEventPayloadIssueCommentSerializer extends StdSerializer<GHEventPayload.IssueComment> {

        public GHEventPayloadIssueCommentSerializer() {
            this(null);
        }

        public GHEventPayloadIssueCommentSerializer(Class<GHEventPayload.IssueComment> t) {
            super(t);
        }

        @Override
        public void serialize(
                GHEventPayload.IssueComment comment, JsonGenerator jgen, SerializerProvider provider)
                throws IOException, JsonProcessingException {

            jgen.writeStartObject();
//            jgen.writeObjectField("comment", comment.getComment());
//            jgen.writeObjectField("issue", comment.getIssue());
            jgen.writeStringField("action", comment.getAction());
//            jgen.writeObjectField("sender", comment.getSender());
//            jgen.writeObjectField("repository", comment.getRepository());
            jgen.writeObjectField("organization", comment.getOrganization());
            jgen.writeObjectField("installation", comment.getInstallation());

            jgen.writeEndObject();
        }
    }

    public class GHUserSerializer extends StdSerializer<GHUser> {

        public GHUserSerializer() {
            this(null);
        }

        public GHUserSerializer(Class<GHUser> t) {
            super(t);
        }

        @Override
        public void serialize(
                GHUser user, JsonGenerator jgen, SerializerProvider provider)
                throws IOException, JsonProcessingException {

            jgen.writeStartObject();
            jgen.writeStringField("bio", user.getBio());
            jgen.writeStringField("name", user.getName());
            jgen.writeEndObject();
        }
    }
    public class GHIssueSerializer extends StdSerializer<GHIssue> {

        public GHIssueSerializer() {
            this(null);
        }

        public GHIssueSerializer(Class<GHIssue> t) {
            super(t);
        }

        @Override
        public void serialize(
                GHIssue issue, JsonGenerator jgen, SerializerProvider provider)
                throws IOException, JsonProcessingException {

            jgen.writeStartObject();
            jgen.writeStringField("title", issue.getTitle());
            jgen.writeStringField("body", issue.getBody());
            jgen.writeEndObject();
        }
    }

    // there might be (probably will be) fields that the bot does not have permission to read.
    // We want to ignore those fields when we serialize the payload to redis by ignoring the IO exceptions
    // from the github api
    // We do this by registering a custom modifier
    // TODO:: this affects the global ObjectMapper registered with Arc, we will want to look at a custom mapper just for redis serialization
    private void disableObjectMapperIOExceptions(){
        ObjectMapper managedMapper = Arc.container().instance(ObjectMapper.class).get();
        if (managedMapper == null) {
            throw new IllegalStateException("There was no ObjectMapper bean configured");
        }

        managedMapper.registerModule(new SimpleModule().setSerializerModifier(new BeanSerializerModifier() {
            @Override
            public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
                return beanProperties.stream().map(bpw -> new BeanPropertyWriter(bpw) {
                    @Override
                    public void serializeAsField(Object bean, JsonGenerator gen, SerializerProvider prov) throws Exception {
                        try {
                            super.serializeAsField(bean, gen, prov);
                        } catch (Exception e) {
                            System.out.println(String.format("ignoring %s for field '%s' of %s instance", e.getClass().getName(), this.getName(), bean.getClass().getName()));
                        }
                    }
                }).collect(Collectors.toList());
            }
        }));
    }



    @CacheResult(cacheName = RedisEventCache.cacheName)
    public String dummyCache(){
        //trick quarkus to create a cache for us
        return null;
    }

    @Override
    public void put(String key, JsonObject eventPayload) {
        String finalResult = eventPayload.toString();
        redisCache.put(key, () -> finalResult).await().indefinitely();
    }

    @Override
    public JsonObject get(String key) {
        String result = redisCache.getOrNull(key, String.class).await().indefinitely();
        return new JsonObject(result);
    }

    @Override
    public Set<String> keys() {
        Iterator<String> iter = redisDataSource.key().scan().toIterable().iterator();
        Set<String> result = new TreeSet<>();
        while (iter.hasNext()) {
            result.add(iter.next());
        }
        return result;

    }
}
