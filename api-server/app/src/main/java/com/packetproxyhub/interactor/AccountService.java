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

import com.google.inject.Inject;
import com.packetproxyhub.entity.*;
import com.packetproxyhub.entity.AccessChecker.AccessChecker;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class AccountService implements IAccountService
{
    @Inject
    private IRepository repository;

    public SessionKey login(SAML saml) throws Exception {
        if (saml.verify()) {
            Name name = saml.name();
            Mail mail = saml.mail();
            return login(name, mail);
        }
        return null;
    }

    @Override
    public synchronized SessionKey login(Name name, Mail mail) throws Exception {
        Account account = repository.getAccount(mail);
        if (account == null) {
            // create a new account
            account = Account.create(name, mail);
            repository.insertAccount(account);
        }
        return account.sessionContext().toSessionKey();
    }

    public Account findAccountBy(SessionKey sessionKey) throws Exception {
        Id accountId = SessionContext.createFromSessionKey(sessionKey).getAccountId();
        Account account = repository.getAccount(accountId);
        if (account == null)
            throw new IllegalAccessException();
        return account;
    }

    @Override
    public Ids listAccounts() throws Exception {
        return repository.listAccounts();
    }

    @Override
    public Account getAccount(Id myAccountId, Id reqAccountId) throws Exception {
        Account account = repository.getAccount(reqAccountId);
        return myAccountId.equals(reqAccountId) ? account : account.removeSensitiveInfo();
    }

    @Override
    public Accounts getAccounts(Id myAccountId) throws Exception {
        Ids accountIds = repository.listAccounts();
        Set<Account> accountSet = accountIds.set().stream().map(accountId -> {
            try {
                return getAccount(myAccountId, accountId);
            } catch (Exception e) {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toSet());
        return Accounts.createWithInit(accountSet);
    }

    @Override
    public Accounts searchAccount(String nameKey) throws Exception {
        return repository.searchAccount(nameKey);
    }

    @Override
    public Id createAccount(Account account) throws Exception {
        return repository.insertAccount(account);
    }

    @Override
    public void updateAccount(Id myAccountId, Account account) throws Exception {
        if (! myAccountId.equals(account.getId())) {
            throw new IllegalAccessException();
        }
        repository.updateAccount(account);
    }

    @Override
    public void removeAccount(Id myAccountId, Id delAccountId) throws Exception {
        if (! myAccountId.equals(delAccountId)) {
            throw new IllegalAccessException();
        }
        repository.deleteAccount(delAccountId);
    }

}
