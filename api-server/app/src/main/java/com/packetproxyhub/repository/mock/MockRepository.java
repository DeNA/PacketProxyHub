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
package com.packetproxyhub.repository.mock;

import com.packetproxyhub.entity.*;
import com.packetproxyhub.interactor.IRepository;
import org.apache.commons.collections.map.MultiValueMap;
import org.apache.ibatis.exceptions.PersistenceException;

import java.util.*;
import java.util.stream.Collectors;

public class MockRepository implements IRepository {
    Set<Account> accounts = new HashSet<>();
    Set<Org> orgs = new HashSet<>();
    Set<Project> projects = new HashSet<>();
    Set<Config> configs = new HashSet<>();
    Set<OrgMember> orgMembers = new HashSet<>();
    Map<Id, Object> projectConfigs = MultiValueMap.decorate(new HashMap<Id, Id>());
    Map<Id, Object> projectAccounts = MultiValueMap.decorate(new HashMap<Id, Id>());
    Map<Id, Object> orgProjects = MultiValueMap.decorate(new HashMap<Id, Id>());
    Map<Id, Object> orgOrgMembers = MultiValueMap.decorate(new HashMap<Id, Id>());

    @Override
    public Org getOrg(Id orgId) {
        return orgs.stream().
                filter(org -> org.getId().equals(orgId)).
                findFirst().
                orElse(null);
    }

    @Override
    public Org getOrg(Name orgName) {
        return orgs.stream().
                filter(org -> org.getName().equals(orgName)).
                findFirst().
                orElse(null);
    }

    @Override
    public Id insertOrg(Org org) {
        if (orgs.stream().anyMatch(o -> o.getName().equals(org.getName()))) {
            throw new PersistenceException();
        }
        orgs.add(org);
        return org.getId();
    }

    @Override
    public void updateOrg(Org org) {
        if (orgs.stream().anyMatch(o -> o.getId().equals(org.getId()))) {
            deleteOrg(org.getId());
            insertOrg(org);
        }
    }

    @Override
    public void deleteOrg(Id orgId) {
        // Orgを消す
        orgs.removeIf(org -> org.getId().equals(orgId));

        // Projectを消す
        List<Id> projectIds = (List<Id>) orgProjects.get(orgId);
        List<Project> prjs = projects.stream().filter(project -> projectIds.contains(project.getId())).collect(Collectors.toList());
        prjs.forEach(project -> removeProjectFromOrg(orgId, project.getId()));
        orgProjects.remove(orgId);

        // OrgMemberを消す
        List<Id> orgMemberIds = (List<Id>)orgOrgMembers.get(orgId);
        List<OrgMember> oms = orgMembers.stream().filter(orgMember -> orgMemberIds.contains(orgMember.getId())).collect(Collectors.toList());
        oms.forEach(orgMember -> removeOrgMemberFromOrg(orgId, orgMember.getId()));
        orgOrgMembers.remove(orgId);
    }

    @Override
    public Ids listOrgFromOrgMembers(Ids myOrgMemberIds) {
        Set<Id> idSet = orgOrgMembers.
                entrySet().
                stream().
                filter(a -> {
                    List<Id> orgMemberIds = (List<Id>) a.getValue();
                    return orgMemberIds.stream().anyMatch(id -> myOrgMemberIds.contains(id));
                }).
                map(a -> a.getKey()).
                collect(Collectors.toSet());
        return Ids.createWithInit(idSet);
    }

    @Override
    public Ids listOrgMembersInOrg(Id orgId) {
        List<Id> idList = (List<Id>)orgOrgMembers.get(orgId);
        if (idList == null)
            return Ids.create();
        Set<Id> idSet = idList.stream().collect(Collectors.toSet());
        return Ids.createWithInit(idSet);
    }

    @Override
    public OrgMember getOrgMember(Id orgMemberId) {
        return orgMembers.
                stream().
                filter(a -> a.getId().equals(orgMemberId)).
                findFirst().
                orElse(null);
    }

    @Override
    public OrgMembers getOrgMembersFromAccount(Id accountId) {
        return OrgMembers.create(orgMembers.stream().filter(a -> a.getAccountId().equals(accountId)).collect(Collectors.toSet()));
    }

    @Override
    public void updateOrgMember(Id orgId, OrgMember orgMember) {
        if (orgMembers.stream().anyMatch(a -> a.getId().equals(orgMember.getId()))) {
            orgMembers.removeIf(p -> p.getId().equals(orgMember.getId()));
            orgMembers.add(orgMember);
        }
    }

    @Override
    public Id insertOrgMemberToOrg(Id orgId, OrgMember orgMember) {
        orgMembers.add(orgMember);
        orgOrgMembers.put(orgId, orgMember.getId());
        return orgMember.getId();
    }

    @Override
    public void removeOrgMemberFromOrg(Id orgId, Id orgMemberId) {
        List<Id> orgMemberIds = (List<Id>) orgOrgMembers.get(orgId);
        orgMemberIds.removeIf(id -> id.equals(orgMemberId));
        orgMembers.removeIf(p -> p.getId().equals(orgMemberId));
    }

    @Override
    public Ids listAccounts() {
        return Ids.createWithInit(accounts.stream().map(a -> a.getId()).collect(Collectors.toSet()));
    }

    @Override
    public Account getAccount(Id accountId) {
        Optional<Account> optionalAccount = accounts.stream().filter(
                a -> a.getId().equals(accountId)
        ).findFirst();
        return optionalAccount.orElse(null);
    }

    @Override
    public Account getAccount(Mail mail) {
        Optional<Account> optionalAccount = accounts.stream().filter(
                a -> a.getMail().equals(mail)
        ).findFirst();
        return optionalAccount.orElse(null);
    }

    @Override
    public Id insertAccount(Account account) {
        if (accounts.stream().anyMatch(a -> a.getMail().equals(account.getMail()))) {
            throw new PersistenceException();
        }
        accounts.add(account);
        return account.getId();
    }

    @Override
    public void updateAccount(Account account) {
        if (accounts.stream().anyMatch(a -> a.getId().equals(account.getId()))) {
            deleteAccount(account.getId());
            insertAccount(account);
        }
    }

    @Override
    public void deleteAccount(Id accountId) {
        accounts.removeIf(a -> a.getId().equals(accountId));
        orgMembers.removeIf(orgMember -> orgMember.getAccountId().equals(accountId));
    }

    @Override
    public Accounts searchAccount(String nameKey) {
        Set<Account> accountSet = accounts.stream().filter(a -> a.getName().toString().matches(String.format(".*%s.*", nameKey))).collect(Collectors.toSet());
        return Accounts.createWithInit(accountSet);
    }

    @Override
    public Id insertProjectToOrg(Id orgId, Project project) {
        projects.add(project);
        orgProjects.put(orgId, project.getId());
        return project.getId();
    }

    @Override
    public void removeProjectFromOrg(Id orgId, Id projectId) {
        List<Id> projectIds = (List<Id>) orgProjects.get(orgId);
        projectIds.removeIf(id -> id.equals(projectId));
        projects.removeIf(p -> p.getId().equals(projectId));

        List<Id> configIds = (List<Id>) projectConfigs.get(projectId);
        List<Config> cfgs = configs.stream().filter(config -> configIds.contains(config.getId())).collect(Collectors.toList());
        cfgs.forEach(config -> removeConfigFromProject(projectId, config.getId()));
        projectConfigs.remove(orgId);
    }

    @Override
    public Ids listProjectsInOrg(Id orgId) {
        List<Id> idList = (List<Id>)orgProjects.get(orgId);
        if (idList == null)
            return Ids.create();
        Set<Id> idSet = idList.stream().collect(Collectors.toSet());
        return Ids.createWithInit(idSet);
    }

    @Override
    public Project getProject(Id projectId) {
        return projects.
                stream().
                filter(a -> a.getId().equals(projectId)).
                findFirst().
                orElse(null);
    }

    @Override
    public void updateProject(Id orgId, Project project) {
        if (projects.stream().anyMatch(a -> a.getId().equals(project.getId()))) {
            projects.removeIf(p -> p.getId().equals(project.getId()));
            projects.add(project);
        }
    }

    @Override
    public Ids listConfigsInProject(Id projectId) {
        List<Id> idList = (List<Id>) projectConfigs.get(projectId);
        if (idList == null) {
            return Ids.create();
        }
        Set<Id> idSet = new HashSet<>(idList);
        return Ids.createWithInit(idSet);
    }

    @Override
    public Config getConfig(Id configId) {
        return configs.stream().
                filter(c -> c.getId().equals(configId)).
                findFirst().
                orElse(null);
    }

    @Override
    public Id insertConfigToProject(Id projectId, Config config) {
        configs.add(config);
        projectConfigs.put(projectId, config.getId());
        return config.getId();
    }

    @Override
    public void removeConfigFromProject(Id projectId, Id configId) {
        List<Id> idList = (List<Id>) projectConfigs.get(projectId);
        idList.removeIf(id -> id.equals(configId));
        projectConfigs.remove(projectId);
        projectConfigs.put(projectId, idList);
        configs.removeIf(c -> c.getId().equals(configId));
    }

    @Override
    public void updateConfig(Id projectId, Config config) {
        if (configs.stream().anyMatch(c -> c.getId().equals(config.getId()))) {
            configs.removeIf(c -> c.getId().equals(config.getId()));
            configs.add(config);
        }
    }

}
