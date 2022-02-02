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
import lombok.Getter;
import lombok.ToString;

import java.lang.reflect.Type;
import java.util.Date;

@Getter
@ToString
@EqualsAndHashCode
public class Config implements JsonSerializer<Config>, JsonDeserializer<Config>
{
    static private final Gson gson = new GsonBuilder().
            registerTypeAdapter(Config.class, Config.create()).
            registerTypeAdapter(Id.class, Id.create()).
            registerTypeAdapter(Name.class, Name.create()).
            setPrettyPrinting().create();

    static public Config create() {
        return create(Name.create(), "", "", "", "", "");
    }

    static public Config create(Name name, String description, String packetProxyConf, String pfConf, String fridaScript, String memo) {
        return create(Id.create(), name, description, packetProxyConf, pfConf, fridaScript, memo, Id.EMPTY, now());
    }

    static public Config create(Id id, Name name, String description, String packetProxyConf, String pfConf, String fridaScript, String memo, Id accountId, long updatedAt) {
        return new Config(id, name, description, packetProxyConf, pfConf, fridaScript, memo, accountId, updatedAt);
    }

    static public Config createFromJson(String json) {
        return gson.fromJson(json, Config.class);
    }

    static private long now() {
        return new Date().getTime() / 1000L;
    }

    private Id id;
    private Name name;
    private String description;
    private String packetProxyConf;
    private String pfConf;
    private String fridaScript;
    private String memo;
    private Id accountId;
    private long updatedAt;

    private Config(Id id, Name name, String description, String packetProxyConf, String pfConf, String fridaScript, String memo, Id accountId, long updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.packetProxyConf = packetProxyConf;
        this.pfConf = pfConf;
        this.fridaScript = fridaScript;
        this.memo = memo;
        this.accountId = accountId;
        this.updatedAt = updatedAt;
    }

    public Config updatedBy(Id accountId) {
        return Config.create(id, name, description, packetProxyConf, pfConf, fridaScript, memo, accountId, now());
    }

    public boolean equalsId(Id id) {
        return this.id.equals(id);
    }

    @Override
    public JsonElement serialize(Config src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("id", src.id.serialize(src.id, Id.class, context));
        result.add("name", src.name.serialize(src.name, Name.class, context));
        result.add("description", new JsonPrimitive(src.description));
        result.add("packetProxyConf", new JsonPrimitive(src.packetProxyConf));
        result.add("pfConf", new JsonPrimitive(src.pfConf));
        result.add("fridaScript", new JsonPrimitive(src.fridaScript));
        result.add("memo", new JsonPrimitive(src.memo));
        result.add("accountId", src.accountId.serialize(src.accountId, Id.class, context));
        result.add("updatedAt", new JsonPrimitive(src.updatedAt));
        return result;
    }

    @Override
    public Config deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject jsonObject = json.getAsJsonObject();
        Id id = Id.create().deserialize(jsonObject.get("id"), Id.class, context);
        Name name = Name.create().deserialize(jsonObject.get("name"), Name.class, context);
        String description = jsonObject.get("description") != null ? jsonObject.get("description").getAsString() : "";
        String packetProxyConf = jsonObject.get("packetProxyConf") != null ? jsonObject.get("packetProxyConf").getAsString() : "";
        String pfConf = jsonObject.get("pfConf") != null ? jsonObject.get("pfConf").getAsString() : "";
        String fridaScript = jsonObject.get("fridaScript") != null ? jsonObject.get("fridaScript").getAsString() : "";
        String memo = jsonObject.get("memo") != null ? jsonObject.get("memo").getAsString() : "";
        Id accountId = Id.create().deserialize(jsonObject.get("accountId"), Id.class, context);
        long updatedAt = jsonObject.get("updatedAt") != null ? jsonObject.get("updatedAt").getAsLong() : now();
        return new Config(id, name, description, packetProxyConf, pfConf, fridaScript, memo, accountId, updatedAt);
    }

    public String toJson() {
        return gson.toJson(this, Config.class);
    }

}
