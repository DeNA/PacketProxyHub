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
package com.packetproxyhub.repository.sqlite.dao;

import com.packetproxyhub.entity.Account;
import com.packetproxyhub.entity.Mail;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DaoAccount {
    private Long dbId;
    private DaoId id;
    private DaoName name;
    private String mail;
    private String packetProxyAccessToken;

    public DaoAccount() {
    }

    public DaoAccount(Account account) {
        this.dbId = account.getId().toLong();
        this.id = DaoId.create(account.getId());
        this.name = DaoName.create(account.getName());
        this.mail = account.getMail().toString();
        this.packetProxyAccessToken = account.getPacketProxyAccessToken();
    }

    public Account toAccount() {
        return Account.create(id.toId(), name.toName(), Mail.create(mail), packetProxyAccessToken);
    }

}
