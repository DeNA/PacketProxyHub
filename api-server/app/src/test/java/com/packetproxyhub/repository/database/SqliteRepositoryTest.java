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
package com.packetproxyhub.repository.database;

import com.google.common.collect.ImmutableSet;
import com.packetproxyhub.entity.*;
import com.packetproxyhub.repository.database.sqlite.SqliteRepository;
import org.apache.ibatis.exceptions.PersistenceException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class SqliteRepositoryTest {
    private String sqliteFilePath;
    private SqliteRepository repository;

    @BeforeEach
    public void setup() throws Exception {
        sqliteFilePath = "/tmp/" + UUID.randomUUID().toString() + ".db";
        repository = new SqliteRepository(sqliteFilePath);
    }

    @AfterEach
    public void tearDown() {
        SqliteRepository.deleteSqliteFile(sqliteFilePath);
    }

    @Nested
    class 組織に関するテスト {
        @Test
        public void 組織を保存できる() {
            Org org = Org.create(Name.create(), "test");
            assertDoesNotThrow(() -> repository.insertOrg(org));
        }

        @Test
        public void 同じ組織名の組織を保存すると例外が発生する() throws Exception {
            repository.insertOrg(Org.create(Name.create("hoge")));
            assertThrows(
                    PersistenceException.class,
                    () -> repository.insertOrg(Org.create(Name.create("hoge"))));
        }

        @Test
        public void 組織IDで検索できる() {
            Org org = Org.create();
            repository.insertOrg(org);
            Org found = repository.getOrg(org.getId());
            assertEquals(org, found);
        }

        @Test
        public void 組織名で検索できる() {
            Org org = Org.create();
            repository.insertOrg(org);
            Org found = repository.getOrg(org.getName());
            assertEquals(org, found);
        }

        @Test
        public void 存在しない組織IDでは検索できない() {
            repository.insertOrg(Org.create());
            Org found = repository.getOrg(Id.create());
            assertNull(found);
        }

        @Test
        public void 存在しない組織名では検索できない() {
            repository.insertOrg(Org.create());
            Org org = repository.getOrg(Name.create());
            assertNull(org);
        }

        @Test
        public void 複数の組織の中から組織名で検索できる() {
            Org orgAAA = Org.create();
            Org orgBBB = Org.create();
            Org orgCCC = Org.create();
            repository.insertOrg(orgAAA);
            repository.insertOrg(orgBBB);
            repository.insertOrg(orgCCC);
            Org found = repository.getOrg(orgBBB.getName());
            assertEquals(orgBBB, found);
        }

        @Test
        public void アカウントを1つ削除できる() throws Exception {
            Org org = Org.create();
            repository.insertOrg(org);
            repository.deleteOrg(org.getId());
            Org found = repository.getOrg(org.getId());
            assertNull(found);
        }

        @Test
        public void アカウントを更新できる() throws Exception {
            Org org1 = Org.create(Name.create(), "an old message");
            repository.insertOrg(org1);
            Org org2 = Org.create(org1.getId(), Name.create(), "a new message");
            repository.updateOrg(org2);
            Org found = repository.getOrg(org1.getId());
            assertEquals(org2, found);
        }
    }

    @Nested
    class アカウントに関するテスト {
        @Test
        public void アカウントを保存できる() {
            Account account = Account.create(Mail.create("aaa@example.com"));
            assertDoesNotThrow(() -> repository.insertAccount(account));
        }

        @Test
        public void 同じmailを保存すると例外が発生する() throws Exception {
            repository.insertAccount(Account.create(Mail.create("a@b")));
            assertThrows(
                    PersistenceException.class,
                    () -> repository.insertAccount(Account.create(Mail.create("a@b"))));
        }

        @Test
        public void UUIDで検索できる() {
            Account account = Account.create(Mail.create("aaa@example.com"));
            repository.insertAccount(account);
            Account found = repository.getAccount(account.getId());
            assertEquals(account, found);
        }

        @Test
        public void mailで検索できる() {
            Account account = Account.create(Mail.create("aaa@example.com"));
            repository.insertAccount(account);
            Account found = repository.getAccount(account.getMail());
            assertEquals(account, found);
        }

        @Test
        public void 存在しないUUIDでは検索できない() {
            Account account = Account.create(Mail.create("aaa@example.com"));
            repository.insertAccount(account);
            Account found = repository.getAccount(Id.create());
            assertNull(found);
        }

        @Test
        public void 存在しないmailでは検索できない() {
            repository.insertAccount(Account.create(Mail.create("c@d")));
            Account account = repository.getAccount(Mail.create("hoge@exapmle.com"));
            assertNull(account);
        }

        @Test
        public void 複数のアカウントの中からmailで検索できる() {
            Account accountAAA = Account.create(Mail.create("aaa@example.com"));
            Account accountBBB = Account.create(Mail.create("bbb@hoge.com"));
            Account accountCCC = Account.create(Mail.create("ccc@foobar.com"));
            repository.insertAccount(accountAAA);
            repository.insertAccount(accountBBB);
            repository.insertAccount(accountCCC);
            Account found = repository.getAccount(accountBBB.getMail());
            assertEquals(accountBBB, found);
        }

        @Test
        public void アカウントを1つ削除できる() throws Exception {
            Account account = Account.create(Mail.create("aaa@example.com"));
            repository.insertAccount(account);
            repository.deleteAccount(account.getId());
            Account found = repository.getAccount(account.getId());
            assertNull(found);
        }

        @Test
        public void アカウントを更新できる() throws Exception {
            Account account1 = Account.create(Name.create("aaa"), Mail.create("aaa@example.com"), "");
            repository.insertAccount(account1);
            Account account2 = Account.create(account1.getId(), Name.create("bbb"), Mail.create("bbb@example.com"), "");
            repository.updateAccount(account2);
            Account found = repository.getAccount(account1.getId());
            assertEquals(account2.getName(), found.getName()); // 名前は変更可能
            assertEquals(account1.getMail(), found.getMail()); // メールアドレスは変更できない
        }

        @Test
        public void アカウントを名前で検索できる() {
            Account account1 = Account.create(Name.create("yamada.taro"), Mail.create("yamada.taro@example.com"));
            Account account2 = Account.create(Name.create("tanaka.jiro"), Mail.create("tanaka.jiro@example.com"));
            Account account3 = Account.create(Name.create("amemiya.yutaro"), Mail.create("amemiya.yutaro@example.com"));
            Account account4 = Account.create(Name.create("yamaguchi.saburo"), Mail.create("yamaguchi.saburo@example.com"));
            repository.insertAccount(account1);
            repository.insertAccount(account2);
            repository.insertAccount(account3);
            repository.insertAccount(account4);
            Accounts accounts = repository.searchAccount("taro");
            assertEquals(2, accounts.size());
            assertTrue(accounts.set().contains(account1));
            assertTrue(accounts.set().contains(account3));
        }
    }

    @Nested
    class コンフィグに関するテスト {
        private Org org;
        private Project project;

        @BeforeEach
        public void setup() {
            org = Org.create();
            project = Project.createEmpty();
            repository.insertOrg(org);
            repository.insertProjectToOrg(org.getId(), project);
        }

        @Test
        public void コンフィグを保存し検索できる() {
            Config config = Config.create();
            repository.insertConfigToProject(project.getId(), config);
            Config found = repository.getConfig(config.getId());
            assertEquals(config, found);
        }

        @Test
        public void 複数のコンフィグを保存し検索できる() {
            Config config1 = Config.create();
            Config config2 = Config.create();
            Ids configIds = Ids.createWithInit(ImmutableSet.of(config1.getId(), config2.getId()));

            repository.insertConfigToProject(project.getId(), config1);
            repository.insertConfigToProject(project.getId(), config2);

            Ids foundIds = repository.listConfigsInProject(project.getId());
            assertEquals(configIds, foundIds);
        }

        @Test
        public void コンフィグを削除できる() {
            Config config = Config.create();
            repository.insertConfigToProject(project.getId(), config);
            repository.removeConfigFromProject(project.getId(), config.getId());
            Config found = repository.getConfig(config.getId());
            assertNull(found);
        }

    }

    @Nested
    class プロジェクトに関するテスト {
        @Test
        public void プロジェクトを保存し検索できる() {
            Org org = Org.create();
            Project project = Project.create("a", "b", "c");
            repository.insertOrg(org);
            Id projectId = repository.insertProjectToOrg(org.getId(), project);
            Project found = repository.getProject(projectId);
            assertEquals(project, found);
        }

        @Test
        public void 存在しないプロジェクトは検索できない() {
            Org org = Org.create();
            Project project = Project.create("a", "b", "c");
            repository.insertOrg(org);
            repository.insertProjectToOrg(org.getId(), project);
            Project notFound = repository.getProject(Id.create());
            assertNull(notFound);
        }

        @Test
        public void プロジェクトを更新できる() {
            Org org = Org.create();
            Project project1 = Project.create("aaa1", "bbb1", "ccc1");
            Project project2 = Project.create(project1.getId(), "aaa2", "bbb2", "ccc2");
            repository.insertOrg(org);
            repository.insertProjectToOrg(org.getId(), project1);
            repository.updateProject(org.getId(), project2);
            Project found = repository.getProject(project1.getId());
            assertEquals(project2.getId(), found.getId());
            assertEquals(project2.getName(), found.getName());
        }

        @Test
        public void プロジェクトを削除できる() {
            Org org = Org.create();
            Project project = Project.create("aaa", "bbb", "ccc");
            repository.insertOrg(org);
            repository.insertProjectToOrg(org.getId(), project);
            repository.removeProjectFromOrg(org.getId(), project.getId());
            Project found = repository.getProject(project.getId());
            assertNull(found);
        }
    }

    @Nested
    class 組織とプロジェクトに関するテスト {
        private Org org;
        private Project project;

        @BeforeEach
        public void setup() {
            org = Org.create();
            project = Project.create("a", "b", "c");
            repository.insertOrg(org);
            repository.insertProjectToOrg(org.getId(), project);
        }

        @Test
        public void 組織IDでプロジェクトを検索できる() {
            Ids foundIds = repository.listProjectsInOrg(org.getId());
            assertEquals(Ids.createWithInit(ImmutableSet.of(project.getId())), foundIds);
        }

        @Test
        public void 組織とプロジェクトの繋がりを削除できる() {
            repository.removeProjectFromOrg(org.getId(), project.getId());
            Ids foundIds = repository.listProjectsInOrg(org.getId());
            assertFalse(foundIds.contains(project.getId()));
        }

        @Test
        public void 他人のプロジェクトは検索できない() {
            Account outsiderAccount = Account.create(Mail.create("outsider@outsiders.com"));
            Ids found = repository.listProjectsInOrg(outsiderAccount.getId());
            assertEquals(0, found.size());
        }

        @Test
        public void 組織を消すとプロジェクトも消える() {
            repository.deleteOrg(org.getId());
            Ids foundIds = repository.listProjectsInOrg(org.getId());
            assertEquals(0, foundIds.size());
        }
    }

    @Nested
    class 組織メンバーに関するテスト {
        private Account account;
        private OrgMember orgMember;
        private Org org;

        @BeforeEach
        public void setup() {
            account = Account.create();
            orgMember = OrgMember.create(account.getId());
            org = Org.create();
            repository.insertAccount(account);
            repository.insertOrg(org);
            repository.insertOrgMemberToOrg(org.getId(), orgMember);
        }

        @Test
        public void アカウントが所属する組織を検索できる() {
            OrgMembers orgMembers = repository.getOrgMembersFromAccount(account.getId());
            assertEquals(1, orgMembers.set().size());
        }

        @Test
        public void アカウントが所属する複数組織を検索できる() {
            OrgMember orgMember2 = OrgMember.create(account.getId());
            Org org2 = Org.create();
            repository.insertOrg(org2);
            repository.insertOrgMemberToOrg(org2.getId(), orgMember2);

            OrgMembers orgMembers = repository.getOrgMembersFromAccount(account.getId());
            assertEquals(2, orgMembers.set().size());
        }

        @Test
        public void 組織メンバーが所属する組織を検索できる() {
            Ids foundIds = repository.listOrgFromOrgMembers(Ids.createWithInit(orgMember.getId()));
            assertEquals(Ids.createWithInit(ImmutableSet.of(org.getId())), foundIds);
        }

        @Test
        public void 組織メンバーを組織から削除できる() {
            repository.removeOrgMemberFromOrg(org.getId(), orgMember.getId());
            Ids foundIds = repository.listOrgMembersInOrg(orgMember.getId());
            assertFalse(foundIds.contains(org.getId()));
        }

        @Test
        public void 組織メンバーではないとき組織を検索できない() {
            Account outsiderAccount = Account.create();
            OrgMember outsiderOrgMember = OrgMember.create(outsiderAccount.getId());
            Ids found = repository.listOrgFromOrgMembers(Ids.createWithInit(outsiderOrgMember.getId()));
            assertEquals(0, found.size());
        }

        @Test
        public void 組織を削除すると組織メンバーも削除される() {
            repository.deleteOrg(org.getId());
            Ids orgMemberIds = repository.listOrgMembersInOrg(org.getId());
            assertEquals(0, orgMemberIds.size());
        }
    }

    @Nested
    class プロジェクトとコンフィグに関するテスト {
        private Org org;
        private Project project;
        private Config config;

        @BeforeEach
        public void setup() {
            org = Org.create();
            project = Project.create("a", "b", "c");
            config = Config.create(Name.create("a"), "b", "c", "d", "e");
            repository.insertOrg(org);
            repository.insertProjectToOrg(org.getId(), project);
            repository.insertConfigToProject(project.getId(), config);
        }

        @Test
        public void プロジェクトを消すとコンフィグが消える() {
            repository.removeProjectFromOrg(org.getId(), project.getId());
            Config notFound = repository.getConfig(config.getId());
            assertNull(notFound);
        }
    }

}
