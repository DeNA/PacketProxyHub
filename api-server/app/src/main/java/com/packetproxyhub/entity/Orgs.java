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
public class Orgs implements JsonSerializer<Orgs>, JsonDeserializer<Orgs>
{
    static private final Gson gson = new GsonBuilder().
            registerTypeAdapter(Orgs.class, Orgs.create()).
            registerTypeAdapter(Org.class, Org.create()).
            setPrettyPrinting().
            create();

    static public Orgs create() {
        return new Orgs();
    }

    static public Orgs createWithInit(Set<Org> orgSet) {
        Orgs orgs = create();
        orgs.add(orgSet);
        return orgs;
    }

    static public Orgs createFromJson(String json) {
        return gson.fromJson(json, Orgs.class);
    }

    private Set<Org> orgs = new HashSet<>();

    public int size() {
        return orgs.size();
    }

    public void add(Org org) {
        orgs.add(org);
    }

    public void add(Set<Org> orgs) {
        this.orgs.addAll(orgs);
    }

    public Set<Org> set() {
        return Collections.unmodifiableSet(orgs);
    }

    @Override
    public JsonElement serialize(Orgs src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray array = new JsonArray();
        for (Org org : src.set()) {
            JsonElement a = org.serialize(org, Org.class, context);
            array.add(a);
        }
        return array;
    }

    @Override
    public Orgs deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Orgs orgs = new Orgs();
        for (JsonElement jsonElement : json.getAsJsonArray()) {
            Org org = Org.create().deserialize(jsonElement, Org.class, context);
            orgs.add(org);
        }
        return orgs;
    }

    public String toJson() {
        return gson.toJson(this, Orgs.class);
    }

}
