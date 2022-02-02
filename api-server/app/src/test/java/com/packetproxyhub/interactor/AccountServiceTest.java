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

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.packetproxyhub.application.App;
import com.packetproxyhub.entity.*;
import com.packetproxyhub.repository.database.mock.MockRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AccountServiceTest {
    static private Injector injector;

    @BeforeAll
    static public void setup() {
        App.setupTest();
        injector = Guice.createInjector(new AbstractModule() {
            @Override protected void configure() {
                bind(IRepository.class).to(MockRepository.class);
            }
        });
    }

    private AccountService accountService;

    @BeforeEach
    public void beforeEachTest() {
        accountService = injector.getInstance(AccountService.class);
    }

    @Disabled
    @Test
    public void loginWithOkta() throws Exception {
        String base64SAML = "saml_token";
        SessionKey sessionKey = accountService.login(new SAML(base64SAML));
        System.out.println(sessionKey.toSessonKeyString());
        System.out.println(SessionContext.createFromSessionKey(sessionKey).toJson());
    }

    @Test
    public void firstTimeLogin() throws Exception {
        SessionKey sessionKey = accountService.login(Name.create("c@d"), Mail.create("c@d"));
        assertTrue(sessionKey != null);
    }

    @Test
    public void multipleLogins() throws Exception {
        SessionKey sessionKey1 = accountService.login(Name.create("c@d"), Mail.create("c@d"));
        SessionKey sessionKey2 = accountService.login(Name.create("c@d"), Mail.create("c@d"));

        SessionContext sessionContext1 = SessionContext.createFromSessionKey(sessionKey1);
        SessionContext sessionContext2 = SessionContext.createFromSessionKey(sessionKey2);
        assertEquals(sessionContext1.getAccountId(), sessionContext2.getAccountId());
    }

    @Test
    public void セッションからAccountに変換できること() throws Exception {
        SessionKey sessionKey = accountService.login(Name.create("c@d"), Mail.create("c@d"));
        Account account = accountService.findAccountBy(sessionKey);
        System.out.println(account.toJson());
    }

}
