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
public class Binaries implements JsonSerializer<Binaries>, JsonDeserializer<Binaries>
{
    static private final Gson gson = new GsonBuilder().
            registerTypeAdapter(Binaries.class, Binaries.create()).
            registerTypeAdapter(Binary.class, Binary.create()).
            setPrettyPrinting()
            .create();

    static public Binaries create() {
        return new Binaries();
    }

    static public Binaries createWithInit(Set<Binary> binarySet) {
        Binaries binaries = Binaries.create();
        binaries.add(binarySet);
        return binaries;
    }

    static public Binaries createFromJson(String json) {
        return gson.fromJson(json, Binaries.class);
    }

    private Set<Binary> binaries = new HashSet<>();

    private Binaries() {
    }

    public void add(Binary binary) {
        binaries.add(binary);
    }

    public void add(Set<Binary> binarySet) {
        binaries.addAll(binarySet);
    }

    public Set<Binary> set() {
        return Collections.unmodifiableSet(binaries);
    }

    public boolean contains(Binary binary) {
        return binaries.stream().anyMatch(c -> c.equals(binary));
    }

    @Override
    public JsonElement serialize(Binaries src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray array = new JsonArray();
        for (Binary binary : src.set()) {
            JsonElement a = context.serialize(binary, Binary.class);
            array.add(a);
        }
        return array;
    }

    @Override
    public Binaries deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Binaries binaries = new Binaries();
        for (JsonElement jsonElement : json.getAsJsonArray()) {
            Binary binary = context.deserialize(jsonElement, Binary.class);
            binaries.add(binary);
        }
        return binaries;
    }

    public String toJson() {
        return gson.toJson(this, Binaries.class);
    }

}
