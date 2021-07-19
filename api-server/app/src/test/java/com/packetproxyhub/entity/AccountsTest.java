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

public class AccountsTest {
    @Test
    public void hashCodeが一致すること() {
        UUID uuid = UUID.randomUUID();
        Account account1 = Account.create(Id.create(uuid), Name.create("aaa"), Mail.create("aaa@bbb.com"), "");
        Account account2 = Account.create(Id.create(uuid), Name.create("ccc"), Mail.create("ccc@ddd.com"), "");
        Accounts accounts1 = Accounts.create();
        accounts1.add(account1);
        accounts1.add(account2);
        Accounts accounts2 = Accounts.create();
        accounts2.add(account1);
        accounts2.add(account2);
        assertEquals(accounts1.hashCode(), accounts2.hashCode());
    }

    @Test
    public void equalsが等しいこと() {
        UUID uuid = UUID.randomUUID();
        Account account1 = Account.create(Id.create(uuid), Name.create("aaa"), Mail.create("aaa@bbb.com"), "");
        Account account2 = Account.create(Id.create(uuid), Name.create("ccc"), Mail.create("ccc@ddd.com"), "");
        Accounts accounts1 = Accounts.create();
        accounts1.add(account1);
        accounts1.add(account2);
        Accounts accounts2 = Accounts.create();
        accounts2.add(account1);
        accounts2.add(account2);
        assertEquals(accounts1, accounts2);
    }

    @Test
    public void 同じAccountは登録できないこと() {
        UUID uuid = UUID.randomUUID();
        Account account1 = Account.create(Id.create(uuid), Name.create("aaa"), Mail.create("aaa@bbb.com"), "");
        Account account2 = Account.create(Id.create(uuid), Name.create("aaa"), Mail.create("aaa@bbb.com"), "");
        Accounts accounts = Accounts.create();
        accounts.add(account1);
        accounts.add(account2);
        assertEquals(1, accounts.size());
    }

    @Test
    public void オブジェクトからJsonそしてJsonからオブジェクトへの変換ができること() throws Exception {
        Accounts accounts = Accounts.create();
        accounts.add(Account.create(Id.create(), Name.create("aaa"), Mail.create("aaa@example.com"), ""));
        accounts.add(Account.create(Id.create(), Name.create("bbb"), Mail.create("bbb@example.com"), ""));
        accounts.add(Account.create(Id.create(), Name.create("ccc"), Mail.create("ccc@example.com"), ""));
        System.out.println(accounts.toJson());
        Accounts restored = Accounts.createFromJson(accounts.toJson());
        assertEquals(accounts, restored);
    }

}
