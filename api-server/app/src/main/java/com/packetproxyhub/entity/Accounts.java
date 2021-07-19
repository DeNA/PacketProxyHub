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
public class Accounts implements JsonSerializer<Accounts>, JsonDeserializer<Accounts>
{
    static private final Gson gson = new GsonBuilder().
            registerTypeAdapter(Accounts.class, Accounts.create()).
            registerTypeAdapter(Account.class, Account.create()).
            setPrettyPrinting().
            create();

    static public GsonBuilder registerTypeAdapter(GsonBuilder builder) {
        return builder.registerTypeAdapter(Accounts.class, Accounts.create());
    }

    static public Accounts createWithInit(Account account) {
        Accounts accounts = new Accounts();
        accounts.add(account);
        return accounts;
    }

    static public Accounts createWithInit(Set<Account> accountSet) {
        Accounts accounts = new Accounts();
        accounts.add(accountSet);
        return accounts;
    }

    static public Accounts create() {
        return new Accounts();
    }

    static public Accounts createFromJson(String json) {
        return gson.fromJson(json, Accounts.class);
    }

    private Set<Account> accounts = new HashSet<>();

    private Accounts() {
    }

    public boolean contains(Account account) {
        return accounts.stream().anyMatch(a -> a.equals(account));
    }

    public void add(Account account) {
        accounts.add(account);
    }

    public void add(Set<Account> accounts) {
        this.accounts.addAll(accounts);
    }

    public int size() {
        return accounts.size();
    }

    public Set<Account> set() {
        return Collections.unmodifiableSet(accounts);
    }

    @Override
    public JsonElement serialize(Accounts src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray array = new JsonArray();
        for (Account account : src.set()) {
            JsonElement a = account.serialize(account, Account.class, context);
            array.add(a);
        }
        return array;
    }

    @Override
    public Accounts deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Accounts accounts = new Accounts();
        for (JsonElement jsonElement : json.getAsJsonArray()) {
            Account account = context.deserialize(jsonElement, Account.class);
            accounts.add(account);
        }
        return accounts;
    }

    public String toJson() {
        return gson.toJson(this, Accounts.class);
    }

}
