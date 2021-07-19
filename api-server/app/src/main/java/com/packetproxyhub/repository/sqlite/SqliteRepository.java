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
package com.packetproxyhub.repository.sqlite;

import com.packetproxyhub.application.App;
import com.packetproxyhub.entity.*;
import com.packetproxyhub.interactor.IRepository;
import com.packetproxyhub.repository.sqlite.dao.*;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

public class SqliteRepository implements IRepository {

    private SqlSessionFactory sqlSessionFactory;

    public SqliteRepository() throws Exception {
        this(App.settings.getDatabasePath());
    }

    public SqliteRepository(String sqliteFilePath) throws Exception {
        File parentDir = new File(sqliteFilePath).getParentFile();
        parentDir.mkdirs();

        Properties props = new Properties();
        props.setProperty("SQLITE_FILE_PATH", sqliteFilePath);

        String resource = "com/packetproxyhub/mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);

        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream, props);
        createTables();
    }

    static public void deleteSqliteFile(String sqliteFilePath) {
        File sqliteFile = new File(sqliteFilePath);
        if (sqliteFile.exists()) {
            sqliteFile.delete();
        }
    }

    private void createTables() {
        SqlSession session = sqlSessionFactory.openSession();

        AccountMapper accountMapper = session.getMapper(AccountMapper.class);
        OrgMapper orgMapper = session.getMapper(OrgMapper.class);
        ProjectMapper projectMapper = session.getMapper(ProjectMapper.class);
        ConfigMapper configMapper = session.getMapper(ConfigMapper.class);
        OrgMemberMapper orgMemberMapper = session.getMapper(OrgMemberMapper.class);

        accountMapper.createTable();
        orgMapper.createTable();
        projectMapper.createTable();
        configMapper.createTable();
        orgMemberMapper.createTable();

        session.commit();
        session.close();
    }

    @Override
    public synchronized Org getOrg(Id orgId) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            OrgMapper orgMapper = session.getMapper(OrgMapper.class);
            DaoOrg daoOrg = orgMapper.selectById(DaoId.create(orgId));

            session.close();
            if (daoOrg == null)
                return null;
            return daoOrg.toOrg();

        } catch (Exception e) {
            session.close();
            throw e;
        }
    }

    @Override
    public synchronized Org getOrg(Name orgName) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            OrgMapper orgMapper = session.getMapper(OrgMapper.class);
            DaoOrg daoOrg = orgMapper.selectByName(new DaoName(orgName));

            session.close();
            if (daoOrg == null)
                return null;
            return daoOrg.toOrg();

        } catch (Exception e) {
            session.close();
            throw e;
        }
    }

    @Override
    public synchronized Id insertOrg(Org org) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            OrgMapper orgMapper = session.getMapper(OrgMapper.class);
            orgMapper.insert(new DaoOrg(org));

            session.commit();
            session.close();
            return org.getId();

        } catch (Exception e) {
            session.rollback();
            session.close();
            throw e;
        }
    }

    @Override
    public synchronized void updateOrg(Org org) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            OrgMapper orgMapper = session.getMapper(OrgMapper.class);
            orgMapper.update(new DaoOrg(org));

            session.commit();
            session.close();

        } catch (Exception e) {
            session.rollback();
            session.close();
            throw e;
        }
    }

    @Override
    public synchronized void deleteOrg(Id orgId) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            OrgMapper orgMapper = session.getMapper(OrgMapper.class);
            orgMapper.delete(DaoId.create(orgId));

            session.commit();
            session.close();

        } catch (Exception e) {
            session.rollback();
            session.close();
            throw e;
        }
    }

    @Override
    public synchronized Ids listOrgFromOrgMembers(Ids orgMembers) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            OrgMemberMapper orgMemberMapper = session.getMapper(OrgMemberMapper.class);

            Set<DaoId> daoOrgMemberSet = orgMembers.set().stream().map(a -> DaoId.create(a)).collect(Collectors.toSet());
            if (daoOrgMemberSet.isEmpty()) {
                session.close();
                return Ids.create();
            }
            Set<DaoOrgMember> orgMemberSet = orgMemberMapper.selectByIds(daoOrgMemberSet);
            Set<Id> orgIdSet = orgMemberSet.stream().map(a -> a.getOrgId().toId()).collect(Collectors.toSet());

            session.close();
            return Ids.createWithInit(orgIdSet);

        } catch (Exception e) {
            session.close();
            throw e;
        }
    }

    @Override
    public synchronized Ids listOrgMembersInOrg(Id orgId) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            OrgMemberMapper orgMemberMapper = session.getMapper(OrgMemberMapper.class);

            Set<DaoOrgMember> orgMemberSet = orgMemberMapper.selectByOrgId(DaoId.create(orgId));
            Set<Id> orgMemberIdSet = orgMemberSet.stream().map(a -> a.getId().toId()).collect(Collectors.toSet());

            session.close();
            return Ids.createWithInit(orgMemberIdSet);

        } catch (Exception e) {
            session.close();
            throw e;
        }
    }

    @Override
    public synchronized OrgMember getOrgMember(Id orgMemberId) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            OrgMemberMapper orgMemberMapper = session.getMapper(OrgMemberMapper.class);
            DaoOrgMember daoOrgMember = orgMemberMapper.select(DaoId.create(orgMemberId));

            session.close();
            if (daoOrgMember == null) {
                return null;
            }
            return daoOrgMember.toOrgMember();

        } catch (Exception e) {
            session.close();
            throw e;
        }
    }

    @Override
    public synchronized OrgMembers getOrgMembersFromAccount(Id accountId) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            OrgMemberMapper orgMemberMapper = session.getMapper(OrgMemberMapper.class);
            Set<DaoOrgMember> orgMemberSet = orgMemberMapper.selectByAccountId(DaoId.create(accountId));

            session.close();
            return DaoOrgMember.toOrgMembers(orgMemberSet);

        } catch (Exception e) {
            session.close();
            throw e;
        }
    }

    @Override
    public synchronized void updateOrgMember(Id orgId, OrgMember orgMember) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            OrgMemberMapper orgMemberMapper = session.getMapper(OrgMemberMapper.class);
            orgMemberMapper.update(DaoOrgMember.create(orgId, orgMember));

            session.commit();
            session.close();

        } catch (Exception e) {
            session.rollback();
            session.close();
            throw e;
        }
    }

    @Override
    public synchronized Id insertOrgMemberToOrg(Id orgId, OrgMember orgMember) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            OrgMemberMapper orgMemberMapper = session.getMapper(OrgMemberMapper.class);
            orgMemberMapper.insert(DaoOrgMember.create(orgId, orgMember));

            session.commit();
            session.close();
            return orgMember.getId();

        } catch (Exception e) {
            session.rollback();
            session.close();
            throw e;
        }
    }

    @Override
    public synchronized void removeOrgMemberFromOrg(Id orgId, Id orgMemberId) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            OrgMemberMapper orgMemberMapper = session.getMapper(OrgMemberMapper.class);
            orgMemberMapper.delete(DaoId.create(orgMemberId));

            session.commit();
            session.close();

        } catch (Exception e) {
            session.rollback();
            session.close();
            throw e;
        }
    }

    @Override
    public synchronized Ids listAccounts() {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            AccountMapper accountMapper = session.getMapper(AccountMapper.class);
            Set<Id> accountIds = accountMapper.selectAll().stream().map(a -> a.toAccount().getId()).collect(Collectors.toSet());

            session.close();
            return Ids.createWithInit(accountIds);

        } catch (Exception e) {
            session.close();
            throw e;
        }
    }

    @Override
    public synchronized Account getAccount(Id accountId) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            AccountMapper accountMapper = session.getMapper(AccountMapper.class);
            DaoAccount daoAccount = accountMapper.selectById(DaoId.create(accountId));

            session.close();
            if (daoAccount == null)
                return null;
            return daoAccount.toAccount();

        } catch (Exception e) {
            session.close();
            throw e;
        }
    }

    @Override
    public synchronized Account getAccount(Mail mail) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            AccountMapper accountMapper = session.getMapper(AccountMapper.class);
            DaoAccount daoAccount = accountMapper.selectByMail(mail.toString());

            session.close();
            if (daoAccount == null)
                return null;
            return daoAccount.toAccount();

        } catch (Exception e) {
            session.close();
            throw e;
        }
    }

    @Override
    public synchronized Id insertAccount(Account account) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            AccountMapper accountMapper = session.getMapper(AccountMapper.class);
            accountMapper.insert(new DaoAccount(account));

            session.commit();
            session.close();
            return account.getId();

        } catch (Exception e) {
            session.rollback();
            session.close();
            throw e;
        }
    }

    @Override
    public synchronized void updateAccount(Account account) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            AccountMapper accountMapper = session.getMapper(AccountMapper.class);
            accountMapper.update(new DaoAccount(account));

            session.commit();
            session.close();

        } catch (Exception e) {
            session.rollback();
            session.close();
            throw e;
        }
    }

    @Override
    public synchronized void deleteAccount(Id accountId) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            AccountMapper accountMapper = session.getMapper(AccountMapper.class);
            accountMapper.delete(DaoId.create(accountId));

            session.commit();
            session.close();

        } catch (Exception e) {
            session.rollback();
            session.close();
            throw e;
        }
    }

    @Override
    public synchronized Accounts searchAccount(String nameKey) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            AccountMapper accountMapper = session.getMapper(AccountMapper.class);
            Set<DaoAccount> daoAccountSet = accountMapper.searchByName(String.format("%%%s%%", nameKey));
            Set<Account> accountSet = daoAccountSet.stream().map(a -> a.toAccount()).collect(Collectors.toSet());
            Accounts accounts = Accounts.createWithInit(accountSet);

            session.close();
            return accounts;

        } catch (Exception e) {
            session.close();
            throw e;
        }
    }

    @Override
    public synchronized Project getProject(Id projectId) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            ProjectMapper projectMapper = session.getMapper(ProjectMapper.class);
            DaoProject daoProject = projectMapper.select(DaoId.create(projectId));

            session.close();
            if (daoProject == null) {
                return null;
            }
            return daoProject.toProject();

        } catch (Exception e) {
            session.close();
            throw e;
        }
    }

    @Override
    public synchronized void updateProject(Id orgId, Project project) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            ProjectMapper projectMapper = session.getMapper(ProjectMapper.class);
            projectMapper.update(new DaoProject(orgId, project));

            session.commit();
            session.close();

        } catch (Exception e) {
            session.rollback();
            session.close();
            throw e;
        }
    }

    @Override
    public synchronized Ids listConfigsInProject(Id projectId) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            ConfigMapper configMapper = session.getMapper(ConfigMapper.class);
            var daoConfigSet = configMapper.selectByProjectId(DaoId.create(projectId));

            session.close();
            return Ids.createWithInit(daoConfigSet.stream().map(a -> a.getId().toId()).collect(Collectors.toSet()));

        } catch (Exception e) {
            session.close();
            throw e;
        }
    }

    @Override
    public synchronized Config getConfig(Id configId) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            ConfigMapper configMapper = session.getMapper(ConfigMapper.class);
            DaoConfig config = configMapper.select(DaoId.create(configId));

            session.close();
            if (config == null) {
                return null;
            }
            return config.toConfig();

        } catch (Exception e) {
            session.close();
            throw e;
        }
    }

    @Override
    public synchronized Id insertConfigToProject(Id projectId, Config config) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            ConfigMapper configMapper = session.getMapper(ConfigMapper.class);
            configMapper.insert(new DaoConfig(projectId, config));

            session.commit();
            session.close();
            return config.getId();

        } catch (Exception e) {
            session.rollback();
            session.close();
            throw e;
        }
    }

    @Override
    public synchronized void removeConfigFromProject(Id projectId, Id configId) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            ConfigMapper configMapper = session.getMapper(ConfigMapper.class);
            configMapper.delete(DaoId.create(configId));

            session.commit();
            session.close();

        } catch (Exception e) {
            session.rollback();
            session.close();
            throw e;
        }
    }

    @Override
    public synchronized void updateConfig(Id projectId, Config config) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            ConfigMapper configMapper = session.getMapper(ConfigMapper.class);
            configMapper.update(new DaoConfig(projectId, config));

            session.commit();
            session.close();

        } catch (Exception e) {
            session.rollback();
            session.close();
            throw e;
        }
    }

    @Override
    public synchronized Ids listProjectsInOrg(Id orgId) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            ProjectMapper projectMapper = session.getMapper(ProjectMapper.class);
            var daoProjectSet = projectMapper.selectByOrgId(DaoId.create(orgId));

            session.close();
            return Ids.createWithInit(daoProjectSet.stream().map(a -> a.getId().toId()).collect(Collectors.toSet()));

        } catch (Exception e) {
            session.close();
            throw e;
        }
    }

    @Override
    public synchronized Id insertProjectToOrg(Id orgId, Project project) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            ProjectMapper projectMapper = session.getMapper(ProjectMapper.class);
            projectMapper.insert(new DaoProject(orgId, project));

            session.commit();
            session.close();
            return project.getId();

        } catch (Exception e) {
            session.rollback();
            session.close();
            throw e;
        }
    }

    @Override
    public synchronized void removeProjectFromOrg(Id orgId, Id projectId) {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            ProjectMapper projectMapper = session.getMapper(ProjectMapper.class);
            projectMapper.delete(DaoId.create(projectId));

            session.commit();
            session.close();

        } catch (Exception e) {
            session.rollback();
            session.close();
            throw e;
        }
    }
}
