package io.hyperfoil.tools.regressionBot.util;


import io.vertx.core.json.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MemEventCache implements EventCache {

    private final Map<String, JsonObject> eventPayloads = new HashMap<>();

    @Override
    public void put(String key, JsonObject eventPayload) {
        eventPayloads.put(key, eventPayload);
    }

    @Override
    public JsonObject get(String key) {
        return eventPayloads.get(key);
    }

    @Override
    public Set<String> keys() {
        return eventPayloads.keySet();
    }
}
