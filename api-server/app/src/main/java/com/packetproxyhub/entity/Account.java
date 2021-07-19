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
public class Account implements JsonSerializer<Account>, JsonDeserializer<Account>
{
    static private final Gson gson = new GsonBuilder().
            registerTypeAdapter(Account.class, Account.create()).
            registerTypeAdapter(Id.class, Id.create()).
            registerTypeAdapter(Name.class, Name.create()).
            registerTypeAdapter(Mail.class, Mail.create()).
            setPrettyPrinting().
            create();

    static public GsonBuilder registerTypeAdapter(GsonBuilder builder) {
        return builder.registerTypeAdapter(Account.class, Account.create());
    }

    static public Account createFromJson(String json) {
        return gson.fromJson(json, Account.class);
    }

    static public Account create() {
        return create(Mail.create());
    }

    static public Account create(Mail mail) {
        return create(Name.create(), mail);
    }

    static public Account create(Name name, Mail mail) {
        return create(name, mail, "");
    }

    static public Account create(Name name, Mail mail, String packetProxyAccessToken) {
        return create(Id.create(), name, mail, packetProxyAccessToken);
    }

    static public Account create(Id id, Name name, Mail mail, String packetProxyAccessToken) {
        return new Account(id, name, mail, packetProxyAccessToken);
    }

    private Id id;
    private Name name;
    private Mail mail;
    private String packetProxyAccessToken;

    private Account(Id id, Name name, Mail mail, String packetProxyAccessToken) {
        this.id = id;
        this.name = name;
        this.mail = mail;
        this.packetProxyAccessToken = packetProxyAccessToken;
    }

    public SessionContext sessionContext() {
        return SessionContext.create(id);
    }

    public Account removeSensitiveInfo() {
        return create(id, name, mail, "");
    }

    @Override
    public JsonElement serialize(Account src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("id", src.id.serialize(src.id, Id.class, context));
        result.add("name", src.name.serialize(src.name, Name.class, context));
        result.add("mail", src.mail.serialize(src.mail, Mail.class, context));
        result.add("packetProxyAccessToken", new JsonPrimitive(src.packetProxyAccessToken));
        return result;
    }

    @Override
    public Account deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        Id id = Id.create().deserialize(jsonObject.get("id"), Id.class, context);
        Name name = Name.create().deserialize(jsonObject.get("name"), Name.class, context);
        Mail mail = Mail.create().deserialize(jsonObject.get("mail"), Mail.class, context);
        String packetProxyAccessToken = jsonObject.get("packetProxyAccessToken") != null ? jsonObject.get("packetProxyAccessToken").getAsString() : "";
        return new Account(id, name, mail, packetProxyAccessToken);
    }

    public String toJson() {
        return gson.toJson(this, Account.class);
    }

}
