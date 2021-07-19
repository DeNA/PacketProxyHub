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
import org.apache.commons.lang3.RandomStringUtils;

import java.lang.reflect.Type;
import java.util.UUID;

@EqualsAndHashCode
public class Name implements JsonSerializer<Name>, JsonDeserializer<Name>
{
    static private final Gson gson = new GsonBuilder().
            registerTypeAdapter(Name.class, Name.create()).
            setPrettyPrinting().
            create();

    static public Name create() {
        return create("Name-" + RandomStringUtils.randomAlphanumeric(10));
    }

    static public Name create(String name) {
        return new Name(name);
    }

    private String name;

    private Name(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public JsonElement serialize(Name src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.name);
    }

    @Override
    public Name deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json == null) {
            return Name.create();
        }
        return Name.create(json.getAsString());
    }

    public String toJson() {
        return gson.toJson(this, Name.class);
    }

}
