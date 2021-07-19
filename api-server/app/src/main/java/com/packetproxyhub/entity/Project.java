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
import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode
public class Project implements JsonSerializer<Project>, JsonDeserializer<Project>
{
    static private final Gson gson = new GsonBuilder().
            registerTypeAdapter(Project.class, Project.createEmpty()).
            setPrettyPrinting().
            create();

    static public Project createEmpty() {
        return create("", "", "");
    }

    static public Project create(String name, String description, String content) {
        return create(Id.create(), name, description, content);
    }

    static public Project create(Id id, String name, String description, String content) {
        return create(id, name, description, content, now());
    }

    static public Project create(Id id, String name, String description, String content, long updatedAt) {
        return new Project(id, name, description, content, updatedAt);
    }

    static public Project createFromJson(String json) {
        return gson.fromJson(json, Project.class);
    }

    static private long now() {
        return new Date().getTime() / 1000L;
    }

    private Id id;
    private String name;
    private String description;
    private String content;
    private long updatedAt;

    private Project(Id id, String name, String description, String content, long updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.content = content;
        this.updatedAt = updatedAt;
    }

    @Override
    public JsonElement serialize(Project src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("id", new JsonPrimitive(src.id.toString()));
        result.add("name", new JsonPrimitive(src.name != null ? src.name : ""));
        result.add("description", new JsonPrimitive(src.description != null ? src.description : ""));
        result.add("content", new JsonPrimitive(src.content != null ? src.content : ""));
        result.add("updatedAt", new JsonPrimitive(src.updatedAt));
        return result;
    }

    @Override
    public Project deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject jsonObject = json.getAsJsonObject();
        Id id = jsonObject.get("id") != null ? Id.create(UUID.fromString(jsonObject.get("id").getAsString())) : Id.create();
        String name = jsonObject.get("name") != null ? jsonObject.get("name").getAsString() : "";
        String description = jsonObject.get("description") != null ? jsonObject.get("description").getAsString() : "";
        String content = jsonObject.get("content") != null ? jsonObject.get("content").getAsString() : "";
        long updatedAt = jsonObject.get("updatedAt") != null ? jsonObject.get("updatedAt").getAsLong() : now();
        return new Project(id, name, description, content, updatedAt);
    }

    public String toJson() {
        return gson.toJson(this, Project.class);
    }

}
