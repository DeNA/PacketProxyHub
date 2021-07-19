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

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.packetproxyhub.application.App;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public class GoogleLogin {

    private GoogleIdTokenVerifier verifier;
    private String idTokenString;
    private GoogleIdToken token;

    public GoogleLogin(String idTokenString) throws Exception {
        NetHttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        this.verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Arrays.asList(App.settings.getGoogleClientId()))
                .setIssuer("accounts.google.com")
                .build();
        this.idTokenString = idTokenString;
    }

    public boolean verify() throws Exception {
        try {
            this.token = this.verifier.verify(this.idTokenString);
            if (this.token == null) {
                return false;
            }
            System.out.println(this.token);
            return this.token.getPayload().getEmailVerified();
        } catch (Exception e) {
            return false;
        }
    }

    public Mail mail() {
        if (this.token == null) {
            return null;
        }
        return Mail.create(this.token.getPayload().getEmail());
    }

    public Name name() {
        if (this.token == null) {
            return null;
        }
        String mail = mail().toString();
        String name = StringUtils.substringBefore(mail, "@");
        return Name.create(name);
    }
}