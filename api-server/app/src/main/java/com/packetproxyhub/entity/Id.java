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
import java.util.UUID;

@EqualsAndHashCode
public class Id implements JsonSerializer<Id>, JsonDeserializer<Id>
{
    static private final Gson gson = new GsonBuilder().
            registerTypeAdapter(Id.class, Id.create()).
            setPrettyPrinting().
            create();

    static public Id EMPTY = create(UUID.fromString("00000000-0000-0000-0000-000000000000"));

    static public Id create() {
        return create(UUID.randomUUID());
    }

    static public Id createFromString(String idStr) {
        return create(UUID.fromString(idStr));
    }

    static public Id create(UUID id) {
        return new Id(id);
    }

    static public Id createFromJson(String json) {
        return gson.fromJson(json, Id.class);
    }

    private UUID id;

    private Id(UUID id) {
        this.id = id;
    }

    public String toString() {
        return id.toString();
    }

    @Override
    public JsonElement serialize(Id src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.id.toString());
    }

    @Override
    public Id deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json == null) {
            return Id.create();
        }
        return Id.create(UUID.fromString(json.getAsString()));
    }

    public String toJson() {
        return gson.toJson(this, Id.class);
    }

    public long toLong() {
        return id.getLeastSignificantBits();
    }
}