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

@Getter
@ToString
@EqualsAndHashCode
public class OrgMember implements JsonSerializer<OrgMember>, JsonDeserializer<OrgMember> {

    static private final Gson gson = new GsonBuilder().
            registerTypeAdapter(OrgMember.class, OrgMember.createEmpty()).
            setPrettyPrinting().
            create();

    public enum Role {
        Owner, Member
    }

    static public OrgMember createEmpty() {
        return create(Id.create());
    }

    static public OrgMember create(Id accountId) {
        return create(accountId, Role.Member);
    }

    static public OrgMember create(Id accountId, Role role) {
        return create(Id.create(), accountId, role);
    }

    static public OrgMember create(Id orgMemberId, Id accountId, Role role) {
        return new OrgMember(orgMemberId, accountId, role);
    }

    static public OrgMember createFromJson(String json) {
        return gson.fromJson(json, OrgMember.class);
    }

    private Id id;
    private Id accountId;
    private Role role;

    private OrgMember() {
    }

    private OrgMember(Id orgMemberId, Id accountId, Role role) {
        this.id = orgMemberId;
        this.accountId = accountId;
        this.role = role;
    }

    @Override
    public JsonElement serialize(OrgMember src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("id", new JsonPrimitive(src.id.toString()));
        result.add("accountId", new JsonPrimitive(src.accountId.toString()));
        result.add("role", new JsonPrimitive(src.role.name()));
        return result;
    }

    @Override
    public OrgMember deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject jsonObject = json.getAsJsonObject();
        Id id = jsonObject.get("id") != null ? Id.createFromString(jsonObject.get("id").getAsString()) : Id.create();
        Id accountId = jsonObject.get("accountId") != null ? Id.createFromString(jsonObject.get("accountId").getAsString()) : Id.create();
        Role role = jsonObject.get("role") != null ? Role.valueOf(jsonObject.get("role").getAsString()) : Role.Member;
        return new OrgMember(id, accountId, role);
    }

    public String toJson() {
        return gson.toJson(this, OrgMember.class);
    }

}
