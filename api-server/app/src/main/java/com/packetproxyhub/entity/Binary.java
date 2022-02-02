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
public class Binary implements JsonSerializer<Binary>, JsonDeserializer<Binary>
{
    static private final Gson gson = new GsonBuilder().
            registerTypeAdapter(Binary.class, Binary.create()).
            registerTypeAdapter(Id.class, Id.create()).
            registerTypeAdapter(Name.class, Name.create()).
            setPrettyPrinting().create();

    static public Binary create() {
        return create(Name.create(), "", Id.create());
    }

    static public Binary create(Name name, String description, Id fileId) {
        return create(Id.create(), name, description, fileId, Id.EMPTY, now());
    }

    static public Binary create(Id id, Name name, String description, Id fileId, Id uploadedBy, long uploadedAt) {
        return new Binary(id, name, description, fileId, uploadedBy, uploadedAt);
    }

    static public Binary createFromJson(String json) {
        return gson.fromJson(json, Binary.class);
    }

    static private long now() {
        return new Date().getTime() / 1000L;
    }

    private Id id;
    private Name name;
    private String description;
    private Id fileId;
    private Id uploadedBy;
    private long uploadedAt;

    private Binary(Id id, Name name, String description, Id fileId, Id uploadedBy, long uploadedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.fileId = fileId;
        this.uploadedBy = uploadedBy;
        this.uploadedAt = uploadedAt;
    }

    public Binary uploadedBy(Id uploadedBy) {
        return Binary.create(id, name, description, fileId, uploadedBy, now());
    }

    public boolean equalsId(Id id) {
        return this.id.equals(id);
    }

    @Override
    public JsonElement serialize(Binary src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("id", src.id.serialize(src.id, Id.class, context));
        result.add("name", src.name.serialize(src.name, Name.class, context));
        result.add("description", new JsonPrimitive(src.description));
        result.add("fileId", src.fileId.serialize(src.fileId, Id.class, context));
        result.add("uploadedBy", src.uploadedBy.serialize(src.uploadedBy, Id.class, context));
        result.add("uploadedAt", new JsonPrimitive(src.uploadedAt));
        return result;
    }

    @Override
    public Binary deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject jsonObject = json.getAsJsonObject();
        Id id = Id.create().deserialize(jsonObject.get("id"), Id.class, context);
        Name name = Name.create().deserialize(jsonObject.get("name"), Name.class, context);
        String description = jsonObject.get("description") != null ? jsonObject.get("description").getAsString() : "";
        Id fileId = Id.create().deserialize(jsonObject.get("fileId"), Id.class, context);
        Id uploadedBy = Id.create().deserialize(jsonObject.get("uploadedBy"), Id.class, context);
        long uploadedAt = jsonObject.get("uploadedAt") != null ? jsonObject.get("uploadedAt").getAsLong() : now();
        return new Binary(id, name, description, fileId, uploadedBy, uploadedAt);
    }

    public String toJson() {
        return gson.toJson(this, Binary.class);
    }
}