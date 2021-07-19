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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@ToString
@EqualsAndHashCode
public class OrgMembers implements JsonSerializer<OrgMembers>, JsonDeserializer<OrgMembers>
{
    static private final Gson gson = new GsonBuilder().
            registerTypeAdapter(OrgMembers.class, OrgMembers.create()).
            registerTypeAdapter(OrgMember.class, OrgMember.createEmpty()).
            setPrettyPrinting()
            .create();

    static public OrgMembers create() {
        return new OrgMembers();
    }

    static public OrgMembers create(Set<OrgMember> orgMemberSet) {
        return new OrgMembers(orgMemberSet);
    }

    static public OrgMembers createFromJson(String json) {
        return gson.fromJson(json, OrgMembers.class);
    }

    private Set<OrgMember> orgMembers = new HashSet<>();

    private OrgMembers() {
    }

    private OrgMembers(Set<OrgMember> orgMembers) {
        this.orgMembers = orgMembers;
    }

    public void add(OrgMember orgMember) {
        orgMembers.add(orgMember);
    }

    public Set<OrgMember> set() {
        return Collections.unmodifiableSet(orgMembers);
    }

    public OrgMembers filterBy(OrgMember.Role role) {
        Set<OrgMember> orgMemberSet = orgMembers.stream().filter(a -> a.getRole().equals(role)).collect(Collectors.toSet());
        return create(orgMemberSet);
    }

    public boolean contains(OrgMember orgMember) {
        return orgMembers.stream().anyMatch(c -> c.equals(orgMember));
    }

    @Override
    public JsonElement serialize(OrgMembers src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray array = new JsonArray();
        for (OrgMember orgMember : src.set()) {
            JsonElement a = context.serialize(orgMember, OrgMember.class);
            array.add(a);
        }
        return array;
    }

    @Override
    public OrgMembers deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        OrgMembers orgMembers = new OrgMembers();
        for (JsonElement jsonElement : json.getAsJsonArray()) {
            OrgMember orgMember = context.deserialize(jsonElement, OrgMember.class);
            orgMembers.add(orgMember);
        }
        return orgMembers;
    }

    public String toJson() {
        return gson.toJson(this, OrgMembers.class);
    }

    public Ids toIds() {
        return Ids.createWithInit(orgMembers.stream().map(a -> a.getId()).collect(Collectors.toSet()));
    }

}
