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
package com.packetproxyhub.interactor;

import com.packetproxyhub.entity.*;

public interface IAccountService {
    SessionKey login(Name name, Mail mail) throws Exception;
    Account findAccountBy(SessionKey sessionKey) throws Exception;

    Ids listAccounts() throws Exception;
    Account getAccount(Id myAccountId, Id reqAccountId) throws Exception;
    Accounts getAccounts(Id myAccountId) throws Exception;
    Accounts searchAccount(String nameKey) throws Exception;
    Id createAccount(Account reqAccount) throws Exception;
    void updateAccount(Id accountId, Account reqAccount) throws Exception;
    void removeAccount(Id accountId, Id delAccountId) throws Exception;

}
