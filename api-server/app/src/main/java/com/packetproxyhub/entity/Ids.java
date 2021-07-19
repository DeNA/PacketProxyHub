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
public class Ids implements JsonSerializer<Ids>, JsonDeserializer<Ids>
{
    static private final Gson gson = new GsonBuilder().
            registerTypeAdapter(Ids.class, Ids.create()).
            registerTypeAdapter(Id.class, Id.create()).
            setPrettyPrinting().
            create();

    static public Ids create() {
        return new Ids();
    }

    static public Ids createWithInit(Id id) {
        Ids ids = create();
        ids.add(id);
        return ids;
    }

    static public Ids createWithInit(Set<Id> idSet) {
        Ids ids = create();
        ids.add(idSet);
        return ids;
    }

    static public Ids createFromJson(String json) {
        return gson.fromJson(json, Ids.class);
    }

    private Set<Id> ids = new HashSet<>();

    public void add(Id id) {
        ids.add(id);
    }

    public void add(Set<Id> idSet) {
        this.ids.addAll(idSet);
    }

    public Set<Id> set() {
        return Collections.unmodifiableSet(ids);
    }

    public boolean contains(Id id) {
        return ids.stream().anyMatch(i -> i.equals(id));
    }

    public int size() {
        return ids.size();
    }

    @Override
    public JsonElement serialize(Ids src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray array = new JsonArray();
        for (Id id : src.set()) {
            JsonElement a = context.serialize(id, Id.class);
            array.add(a);
        }
        return array;
    }

    @Override
    public Ids deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Ids ids = new Ids();
        for (JsonElement jsonElement : json.getAsJsonArray()) {
            Id id = context.deserialize(jsonElement, Id.class);
            ids.add(id);
        }
        return ids;
    }

    public String toJson() {
        return gson.toJson(this, Ids.class);
    }

}
