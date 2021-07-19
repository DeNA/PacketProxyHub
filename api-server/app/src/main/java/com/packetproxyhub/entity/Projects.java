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
public class Projects implements JsonSerializer<Projects>, JsonDeserializer<Projects>
{
    static private final Gson gson = new GsonBuilder().
            registerTypeAdapter(Projects.class, Projects.create()).
            registerTypeAdapter(Project.class, Project.createEmpty()).
            setPrettyPrinting().
            create();

    static public Projects create() {
        return new Projects();
    }

    static public Projects createWithInit(Set<Project> projectSet) {
        Projects projects = create();
        projects.add(projectSet);
        return projects;
    }

    static public Projects createFromJson(String json) {
        return gson.fromJson(json, Projects.class);
    }

    private Set<Project> projects = new HashSet<>();

    public int size() {
        return projects.size();
    }

    public void add(Project project) {
        projects.add(project);
    }

    public void add(Set<Project> projects) {
        this.projects.addAll(projects);
    }

    public Set<Project> set() {
        return Collections.unmodifiableSet(projects);
    }

    @Override
    public JsonElement serialize(Projects src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray array = new JsonArray();
        for (Project project : src.set()) {
            JsonElement a = context.serialize(project, Project.class);
            array.add(a);
        }
        return array;
    }

    @Override
    public Projects deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Projects projects = new Projects();
        for (JsonElement jsonElement : json.getAsJsonArray()) {
            Project project = context.deserialize(jsonElement, Project.class);
            projects.add(project);
        }
        return projects;
    }

    public String toJson() {
        return gson.toJson(this, Projects.class);
    }

}
