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

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountTest {
    @Test
    public void ハッシュが一致すること() {
        UUID uuid = UUID.randomUUID();
        Account account1 = Account.create(Id.create(uuid), Name.create("aaa"), Mail.create("aaa@bbb.com"), "");
        Account account2 = Account.create(Id.create(uuid), Name.create("aaa"), Mail.create("aaa@bbb.com"), "");
        assertEquals(account1.hashCode(), account2.hashCode());
    }

    @Test
    public void equalsが等しいこと() {
        UUID uuid = UUID.randomUUID();
        Account account1 = Account.create(Id.create(uuid), Name.create("aaa"), Mail.create("aaa@bbb.com"), "");
        Account account2 = Account.create(Id.create(uuid), Name.create("aaa"), Mail.create("aaa@bbb.com"), "");
        assertEquals(account1, account2);
    }

    @Test
    public void オブジェクトからJsonそしてJsonからオブジェクトへの変換ができること() throws Exception {
        Account account = Account.create(Id.create(), Name.create("aaa"), Mail.create("aaa@bbb.com"), "");
        System.out.println(account.toJson());
        Account restored = Account.createFromJson(account.toJson());
        assertEquals(account, restored);
    }
}
