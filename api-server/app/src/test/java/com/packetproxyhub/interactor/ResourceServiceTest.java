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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResourceServiceTest {

    private Injector injector;
    private ResourceService resourceService;
    private AccountService accountService;
    private OrgService orgService;
    private ProjectService projectService;
    private ConfigService configService;
    private OrgMemberService orgMemberService;

    @BeforeEach
    public void beforeEachTest() {
        App.setupTest();
        injector = Guice.createInjector(new AbstractModule() {
            @Override protected void configure() {
                bind(IRepository.class).to(MockRepository.class).asEagerSingleton();
                bind(IAccountService.class).to(AccountService.class).asEagerSingleton();
                bind(IResourceService.class).to(ResourceService.class).asEagerSingleton();
                bind(IOrgService.class).to(OrgService.class).asEagerSingleton();
                bind(IProjectService.class).to(ProjectService.class).asEagerSingleton();
                bind(IConfigService.class).to(ConfigService.class).asEagerSingleton();
                bind(IOrgMemberService.class).to(OrgMemberService.class).asEagerSingleton();
                bind(IBinaryService.class).to(BinaryService.class).asEagerSingleton();
                bind(ISnapshotService.class).to(SnapshotService.class).asEagerSingleton();
            }
        });
        resourceService = injector.getInstance(ResourceService.class);
        accountService = injector.getInstance(AccountService.class);
        orgService = injector.getInstance(OrgService.class);
        projectService = injector.getInstance(ProjectService.class);
        configService = injector.getInstance(ConfigService.class);
        orgMemberService = injector.getInstance(OrgMemberService.class);
    }

    @Test
    public void リソースを取得する() throws Exception {
        SessionKey sessionKey1 = accountService.login(Name.create("yamada.taro"), Mail.create("yamada.taro@example.com"));
        SessionKey sessionKey2 = accountService.login(Name.create("sato.ziro"), Mail.create("sato.ziro@example.com"));
        Account account1 = accountService.findAccountBy(sessionKey1);
        Account account2 = accountService.findAccountBy(sessionKey1);
        Org org = Org.create(Name.create("Test Org"));
        orgService.createOrg(account1.getId(), org);
        Project project1 = Project.create("Test Project 1", "desc 1", "content 1");
        Project project2 = Project.create("Test Project 2", "desc 2", "content 2");
        projectService.createProject(account1.getId(), org.getId(), project1);
        projectService.createProject(account1.getId(), org.getId(), project2);
        Config config1 = Config.create(Name.create("Config1"), "name", "2", "3", "4", "5");
        configService.createConfig(account1.getId(), org.getId(), project2.getId(), config1);
        OrgMember member = OrgMember.create(account2.getId());
        orgMemberService.createOrgMember(account1.getId(), org.getId(), member);

        Resource resource = resourceService.getAllResource(account1.getId());

        System.out.println(resource.toJson());
    }

}
