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

import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.packetproxyhub.application.App;
import com.packetproxyhub.entity.*;
import com.packetproxyhub.repository.database.mock.MockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OrgMemberServiceTest {

    private Injector injector;
    private OrgService orgService;
    private OrgMemberService orgMemberService;
    private AccountService accountService;

    @BeforeEach
    public void beforeEachTest() {
        App.setupTest();
        injector = Guice.createInjector(new AbstractModule() {
            @Override protected void configure() {
                bind(IRepository.class).to(MockRepository.class).asEagerSingleton();
                bind(IAccountService.class).to(AccountService.class).asEagerSingleton();
                bind(IOrgMemberService.class).to(OrgMemberService.class).asEagerSingleton();
                bind(IOrgService.class).to(OrgService.class).asEagerSingleton();
            }
        });
        orgMemberService = injector.getInstance(OrgMemberService.class);
        accountService = injector.getInstance(AccountService.class);
        orgService = injector.getInstance(OrgService.class);
    }

    @Test
    public void 組織メンバーを名前で検索する() throws Exception {
        SessionKey sessionKey1 = accountService.login(Name.create("yamada.taro"), Mail.create("yamada.taro@example.com"));
        SessionKey sessionKey2 = accountService.login(Name.create("sato.atsushi"), Mail.create("sato.atsushi@example.com"));
        SessionKey sessionKey3 = accountService.login(Name.create("tanaka.yutaro"), Mail.create("tanaka.yutaro@example.com"));
        Account account1 = accountService.findAccountBy(sessionKey1);
        Account account2 = accountService.findAccountBy(sessionKey2);
        Account account3 = accountService.findAccountBy(sessionKey3);
        Id orgId = orgService.createOrg(account1.getId(), Org.create(Name.create("test team")));
        orgMemberService.createOrgMember(account1.getId(), orgId, OrgMember.create(account2.getId()));
        orgMemberService.createOrgMember(account1.getId(), orgId, OrgMember.create(account3.getId()));

        Accounts accounts = orgMemberService.searchAccount(account1.getId(), orgId, "taro");

        assertTrue(accounts.set().containsAll(ImmutableSet.of(account1, account3)));
    }

}
