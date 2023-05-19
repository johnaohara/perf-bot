package io.hyperfoil.tools.regressionBot.util;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

public class EventCacheProducer {

    private static final Logger LOG = Logger.getLogger(EventCacheProducer.class);

    @ConfigProperty(name = "perf-bot.eventCache.type")
    String cacheType;

    @Produces
    @ApplicationScoped
    EventCache produceCache() {
        switch (cacheType){
            case "mem":
                return new MemEventCache();
            case "redis":
                return new RedisEventCache();
            default:
                LOG.warnf("Could not instantiate cache of type: %s", cacheType);

        }
        return null;
    }
}
