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

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.packetproxyhub.application.App;
import org.apache.commons.codec.binary.Base64;

public class SessionKey {
    static private final byte[] CRYPT_KEY = App.settings.getSessionCryptKey().getBytes();

    @SerializedName("accessToken")
    String sessionKeyString;

    static public SessionKey createFromRaw(String raw) throws Exception {
        Crypt crypt = new Crypt(CRYPT_KEY);
        byte[] encrypted = crypt.encrypt(raw.getBytes());
        return new SessionKey(Base64.encodeBase64URLSafeString(encrypted));
    }

    static public SessionKey createFromSessionKeyString(String sessionKeyString) throws Exception {
        return new SessionKey(sessionKeyString);
    }

    private SessionKey(String sessionKeyString) {
        this.sessionKeyString = sessionKeyString;
    }

    public String toRaw() throws Exception {
        Crypt crypt = new Crypt(CRYPT_KEY);
        byte[] encrypted = Base64.decodeBase64(sessionKeyString);
        byte[] jsonText = crypt.decrypt(encrypted);
        return new String(jsonText);
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public String toSessonKeyString() {
        return sessionKeyString;
    }
}
