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
import lombok.*;

import java.lang.reflect.Type;
import java.util.*;

@Getter
@ToString
@EqualsAndHashCode
public class Resource implements JsonSerializer<Resource> {

    static private final Gson gson = new GsonBuilder().
            registerTypeAdapter(Resource.class, Resource.create()).
            registerTypeAdapter(Accounts.class, Accounts.create()).
            registerTypeAdapter(Orgs.class, Orgs.create()).
            registerTypeAdapter(Projects.class, Projects.create()).
            registerTypeAdapter(Configs.class, Configs.create()).
            registerTypeAdapter(OrgMembers.class, OrgMembers.create()).
            registerTypeAdapter(Id.class, Id.create()).
            registerTypeAdapter(Name.class, Name.create()).
            setPrettyPrinting().
            create();

    static public Resource create() {
        return new Resource();
    }

    @Data @AllArgsConstructor static class StProject { Id orgId; Projects projects; }
    @Data @AllArgsConstructor static class StConfig { Id orgId; Id projectId; Configs configs; }
    @Data @AllArgsConstructor static class StOrgMember { Id orgId; OrgMembers orgMembers; }

    Accounts accounts = Accounts.create();
    Orgs orgs = Orgs.create();
    Set<StProject> projectSet = new HashSet<>();
    Set<StConfig> configSet = new HashSet<>();
    Set<StOrgMember> orgMemberSet = new HashSet<>();

    private Resource() {
    }

    public void setAccounts(Accounts accounts) {
        this.accounts = accounts;
    }

    public void setOrgs(Orgs orgs) {
        this.orgs = orgs;
    }

    public void addProjects(Id orgId, Projects projects) {
        this.projectSet.add(new StProject(orgId, projects));
    }

    public void addConfigs(Id orgId, Id projectId, Configs configs) {
        this.configSet.add(new StConfig(orgId, projectId, configs));
    }

    public void addOrgMembers(Id orgId, OrgMembers orgMembers) {
        this.orgMemberSet.add(new StOrgMember(orgId, orgMembers));
    }

    @Override
    public JsonElement serialize(Resource src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();

        result.add("accounts", src.getAccounts().serialize(src.getAccounts(), Accounts.class, context));
        result.add("orgs", src.getOrgs().serialize(src.getOrgs(), Orgs.class, context));
        result.add("projects", getJsonProjects(src, context));
        result.add("configs", getJsonConfigs(src, context));
        result.add("orgMembers", getJsonOrgMembers(src, context));

        return result;
    }

    private JsonArray getJsonProjects(Resource src, JsonSerializationContext context) {
        JsonArray array = new JsonArray();
        src.projectSet.forEach(stProject -> {
            JsonObject projectJO = new JsonObject();
            projectJO.add("orgId", stProject.getOrgId().serialize(stProject.getOrgId(), Id.class, context));
            projectJO.add("projects", stProject.getProjects().serialize(stProject.getProjects(), Projects.class, context));
            array.add(projectJO);
        });
        return array;
    }

    private JsonArray getJsonConfigs(Resource src, JsonSerializationContext context) {
        JsonArray array = new JsonArray();
        src.configSet.forEach(stConfig -> {
            JsonObject projectJO = new JsonObject();
            projectJO.add("orgId", stConfig.getOrgId().serialize(stConfig.getOrgId(), Id.class, context));
            projectJO.add("projectId", stConfig.getProjectId().serialize(stConfig.getProjectId(), Id.class, context));
            projectJO.add("configs", stConfig.getConfigs().serialize(stConfig.getConfigs(), Configs.class, context));
            array.add(projectJO);
        });
        return array;
    }

    private JsonArray getJsonOrgMembers(Resource src, JsonSerializationContext context) {
        JsonArray array = new JsonArray();
        src.orgMemberSet.forEach(stOrgMember -> {
            JsonObject projectJO = new JsonObject();
            projectJO.add("orgId", stOrgMember.getOrgId().serialize(stOrgMember.getOrgId(), Id.class, context));
            projectJO.add("orgMembers", stOrgMember.getOrgMembers().serialize(stOrgMember.getOrgMembers(), OrgMembers.class, context));
            array.add(projectJO);
        });
        return array;
    }

    public String toJson() {
        return gson.toJson(this, Resource.class);
    }

}
