package io.hyperfoil.tools.regressionBot.svc;


import io.hyperfoil.tools.regressionBot.benchmark.Benchmarks;
import io.hyperfoil.tools.regressionBot.util.EventCache;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import java.util.Set;

@Path("/api")
public class ApiService {

    @Inject
    Benchmarks benchmarks;

    @Inject
    EventCache cache;

    @GET
    @Path("benchmarks/")
    public Set<String> getBenchmarks(){
        return benchmarks.getBechmarkNames();
    }

    @GET
    @Path("event/")
    public Set<String> getKeys(){
        return cache.keys();
    }

    @GET
    @Path("event/{event_key}")
    public JsonObject getEvent(@PathParam("event_key") String eventKey){

        JsonObject result = cache.get(eventKey);

        return result != null ? result : JsonObject.of("{\"msg\": \"Not found\"}", Object.class);
    }

}
