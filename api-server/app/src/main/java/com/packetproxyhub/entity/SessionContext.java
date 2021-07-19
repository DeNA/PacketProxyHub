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

import java.util.Date;
import java.util.UUID;

public class SessionContext {
    private Id accountId;
    private long expiredAt;

    static public SessionContext createFromJson(String jsonText) {
        return new Gson().fromJson(jsonText, SessionContext.class);
    }

    static public SessionContext createFromSessionKey(SessionKey sessionKey) throws Exception {
        return createFromJson(sessionKey.toRaw());
    }

    static public SessionContext create(Id accountId) {
        return new SessionContext(accountId);
    }

    private SessionContext(Id accountId) {
        this.accountId = accountId;
        this.expiredAt = now();
    }

    private long now() {
        return new Date().getTime() / 1000L;
    }

    public boolean isValid() {
        return this.expiredAt > now();
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public SessionKey toSessionKey() throws Exception {
        return SessionKey.createFromRaw(toJson());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SessionContext) {
            SessionContext session = (SessionContext) obj;
            return this.accountId.equals(session.accountId) &&
                    this.expiredAt == session.expiredAt;
        }
        return false;
    }

    public String toString() {
        return String.format("accountId:%s, expiredAt:%d", accountId, expiredAt);
    }

    public Id getAccountId() {
        return accountId;
    }

    public long getExpiredAt() {
        return expiredAt;
    }
}
