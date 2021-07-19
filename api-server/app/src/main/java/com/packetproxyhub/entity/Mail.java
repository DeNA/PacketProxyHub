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
import org.apache.commons.lang3.RandomStringUtils;

import java.lang.reflect.Type;

@EqualsAndHashCode
public final class Mail implements JsonSerializer<Mail>, JsonDeserializer<Mail> {
    static private final Gson gson = new GsonBuilder().
            registerTypeAdapter(Mail.class, Mail.create()).
            setPrettyPrinting().
            create();

    static public Mail create() {
        return create(RandomStringUtils.randomAlphanumeric(10) + "@packetporoxyhub.com");
    }

    static public Mail create(String mail) {
        return new Mail(mail);
    }

    private final String mail;

    private Mail(String mail) {
        this.mail = mail;
    }

    @Override
    public JsonElement serialize(Mail src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.mail);
    }

    @Override
    public Mail deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json == null) {
            return Mail.create();
        }
        return Mail.create(json.getAsString());
    }

    public String toJson() {
        return gson.toJson(this, Mail.class);
    }

    @Override
    public String toString() {
        return mail;
    }
}
