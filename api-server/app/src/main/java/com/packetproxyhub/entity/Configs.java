/*
 * Copyright 2021 DeNA Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.packetproxyhub.entity;

import com.google.gson.*;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@ToString
@EqualsAndHashCode
public class Configs implements JsonSerializer<Configs>, JsonDeserializer<Configs>
{
    static private final Gson gson = new GsonBuilder().
            registerTypeAdapter(Configs.class, Configs.create()).
            registerTypeAdapter(Config.class, Config.create()).
            setPrettyPrinting()
            .create();

    static public Configs create() {
        return new Configs();
    }

    static public Configs createWithInit(Set<Config> configSet) {
        Configs configs = Configs.create();
        configs.add(configSet);
        return configs;
    }

    static public Configs createFromJson(String json) {
        return gson.fromJson(json, Configs.class);
    }

    private Set<Config> configs = new HashSet<>();

    private Configs() {
    }

    public void add(Config config) {
        configs.add(config);
    }

    public void add(Set<Config> configSet) {
        configs.addAll(configSet);
    }

    public Set<Config> set() {
        return Collections.unmodifiableSet(configs);
    }

    public boolean contains(Config config) {
        return configs.stream().anyMatch(c -> c.equals(config));
    }

    @Override
    public JsonElement serialize(Configs src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray array = new JsonArray();
        for (Config config : src.set()) {
            JsonElement a = context.serialize(config, Config.class);
            array.add(a);
        }
        return array;
    }

    @Override
    public Configs deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Configs configs = new Configs();
        for (JsonElement jsonElement : json.getAsJsonArray()) {
            Config config = context.deserialize(jsonElement, Config.class);
            configs.add(config);
        }
        return configs;
    }

    public String toJson() {
        return gson.toJson(this, Configs.class);
    }

}
