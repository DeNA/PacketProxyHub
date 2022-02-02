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
import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode
public class Org implements JsonSerializer<Org>, JsonDeserializer<Org>
{
    static private final Gson gson = new GsonBuilder().
            registerTypeAdapter(Org.class, Org.create()).
            registerTypeAdapter(Name.class, Name.create()).
            registerTypeAdapter(Id.class, Id.create()).
            setPrettyPrinting().
            create();

    static public Org create() {
        return create(Name.create(), "");
    }

    static public Org create(Name name) {
        return create(name, "");
    }

    static public Org create(Name name, String description) {
        return create(Id.create(), name, description);
    }

    static public Org create(Id id, Name name, String description) {
        return new Org(id, name, description);
    }

    static public Org createFromJson(String json) {
        return gson.fromJson(json, Org.class);
    }

    private Id id;
    private Name name;
    private String description;

    private Org(Id id, Name name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    @Override
    public JsonElement serialize(Org src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("id", new JsonPrimitive(src.id.toString()));
        result.add("name", new JsonPrimitive(src.name != null ? src.name.toString() : ""));
        result.add("description", new JsonPrimitive(src.description != null ? src.description : ""));
        return result;
    }

    @Override
    public Org deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject jsonObject = json.getAsJsonObject();
        Id id = jsonObject.get("id") != null ? Id.create(UUID.fromString(jsonObject.get("id").getAsString())) : Id.create();
        Name name = jsonObject.get("name") != null ? Name.create(jsonObject.get("name").getAsString()) : Name.create();
        String description = jsonObject.get("description") != null ? jsonObject.get("description").getAsString() : "";
        return new Org(id, name, description);
    }

    public String toJson() {
        return gson.toJson(this, Org.class);
    }

}
