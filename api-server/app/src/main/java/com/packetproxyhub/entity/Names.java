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
public class Names implements JsonSerializer<Names>, JsonDeserializer<Names>
{
    static private final Gson gson = new GsonBuilder().
            registerTypeAdapter(Names.class, Names.create()).
            registerTypeAdapter(Name.class, Name.create()).
            setPrettyPrinting().
            create();

    static public Names create() {
        return new Names();
    }

    static public Names createFromJson(String json) {
        return gson.fromJson(json, Names.class);
    }

    private Set<Name> names = new HashSet<>();

    public int size() {
        return names.size();
    }

    public void add(Name Name) {
        names.add(Name);
    }

    public void add(Set<Name> names) {
        this.names.addAll(names);
    }

    public Set<Name> set() {
        return Collections.unmodifiableSet(names);
    }

    @Override
    public JsonElement serialize(Names src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray array = new JsonArray();
        for (Name name : src.set()) {
            JsonElement a = context.serialize(name, Name.class);
            array.add(a);
        }
        return array;
    }

    @Override
    public Names deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Names names = new Names();
        for (JsonElement jsonElement : json.getAsJsonArray()) {
            Name name = context.deserialize(jsonElement, Name.class);
            names.add(name);
        }
        return names;
    }

    public String toJson() {
        return gson.toJson(this, Names.class);
    }

}
