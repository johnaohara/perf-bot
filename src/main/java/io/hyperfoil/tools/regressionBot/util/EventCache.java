package io.hyperfoil.tools.regressionBot.util;


import io.vertx.core.json.JsonObject;

import java.util.Set;

public interface EventCache {

    void put (String key, JsonObject eventPayload);
    JsonObject get(String key);
    Set<String> keys();

}
