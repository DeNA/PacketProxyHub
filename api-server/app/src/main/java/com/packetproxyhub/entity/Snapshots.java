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
public class Snapshots implements JsonSerializer<Snapshots>, JsonDeserializer<Snapshots>
{
    static private final Gson gson = new GsonBuilder().
            registerTypeAdapter(Snapshots.class, Snapshots.create()).
            registerTypeAdapter(Snapshot.class, Snapshot.create()).
            setPrettyPrinting()
            .create();

    static public Snapshots create() {
        return new Snapshots();
    }

    static public Snapshots createWithInit(Set<Snapshot> snapshotSet) {
        Snapshots snapshots = Snapshots.create();
        snapshots.add(snapshotSet);
        return snapshots;
    }

    static public Snapshots createFromJson(String json) {
        return gson.fromJson(json, Snapshots.class);
    }

    private Set<Snapshot> snapshots = new HashSet<>();

    private Snapshots() {
    }

    public void add(Snapshot snapshot) {
        snapshots.add(snapshot);
    }

    public void add(Set<Snapshot> snapshotSet) {
        snapshots.addAll(snapshotSet);
    }

    public Set<Snapshot> set() {
        return Collections.unmodifiableSet(snapshots);
    }

    public boolean contains(Snapshot snapshot) {
        return snapshots.stream().anyMatch(c -> c.equals(snapshot));
    }

    @Override
    public JsonElement serialize(Snapshots src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray array = new JsonArray();
        for (Snapshot snapshot : src.set()) {
            JsonElement a = context.serialize(snapshot, Snapshot.class);
            array.add(a);
        }
        return array;
    }

    @Override
    public Snapshots deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Snapshots snapshots = new Snapshots();
        for (JsonElement jsonElement : json.getAsJsonArray()) {
            Snapshot snapshot = context.deserialize(jsonElement, Snapshot.class);
            snapshots.add(snapshot);
        }
        return snapshots;
    }

    public String toJson() {
        return gson.toJson(this, Snapshots.class);
    }

}
