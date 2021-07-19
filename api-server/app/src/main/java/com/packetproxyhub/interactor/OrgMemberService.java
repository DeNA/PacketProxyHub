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
import com.packetproxyhub.entity.AccessChecker.AccessChecker;

import javax.inject.Inject;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class OrgMemberService implements IOrgMemberService {
    @Inject
    private IRepository repository;
    @Inject
    private IAccountService accountService;

    @Override
    public Ids listOrgMembers(Id accountId, Id orgId) throws Exception {
        if (!AccessChecker.create(accountId, repository).isOrgReadable(orgId)) {
            throw new IllegalAccessException();
        }
        return repository.listOrgMembersInOrg(orgId);
    }

    @Override
    public Accounts searchAccount(Id accountId, Id orgId, String nameKey) throws Exception {
        if (!AccessChecker.create(accountId, repository).isOrgReadable(orgId)) {
            throw new IllegalAccessException();
        }
        Ids orgMemberIds = repository.listOrgMembersInOrg(orgId);
        Set<Id> orgMemberAccountIds = orgMemberIds.set().stream().map(orgMemberId -> {
            OrgMember orgMember = repository.getOrgMember(orgMemberId);
            return orgMember.getAccountId();
        }).collect(Collectors.toSet());

        Accounts accounts = accountService.searchAccount(nameKey);
        Set<Account> accountSet = accounts.set().stream().filter(account ->
            orgMemberAccountIds.contains(account.getId())
        ).collect(Collectors.toSet());

        return Accounts.createWithInit(accountSet);
    }

    @Override
    public OrgMember getOrgMember(Id accountId, Id orgId, Id orgMemberId) throws Exception {
        if (!AccessChecker.create(accountId, repository).isOrgReadable(orgId)) {
            throw new IllegalAccessException();
        }
        return repository.getOrgMember(orgMemberId);
    }

    @Override
    public OrgMembers getOrgMembers(Id accountId, Id orgId) throws Exception {
        Ids orgMemberIds = repository.listOrgMembersInOrg(orgId);
        Set<OrgMember> orgMemberSet = orgMemberIds.set().stream().map(orgMemberId -> repository.getOrgMember(orgMemberId)).collect(Collectors.toSet());
        return OrgMembers.create(orgMemberSet);
    }

    @Override
    public Id createOrgMember(Id accountId, Id orgId, OrgMember orgMember) throws Exception {
        if (!AccessChecker.create(accountId, repository).isOrgWritable(orgId)) {
            throw new IllegalAccessException();
        }
        OrgMember newOrgMember = OrgMember.create(
                orgMember.getAccountId(),
                orgMember.getRole());
        return repository.insertOrgMemberToOrg(orgId, newOrgMember);
    }

    @Override
    public void removeOrgMember(Id accountId, Id orgId, Id orgMemberId) throws Exception {
        if (!AccessChecker.create(accountId, repository).isOrgWritable(orgId)) {
            throw new IllegalAccessException();
        }
        repository.removeOrgMemberFromOrg(orgId, orgMemberId);
    }

    @Override
    public void updateOrgMember(Id accountId, Id orgId, Id orgMemberId, OrgMember orgMember) throws Exception {
        if (!AccessChecker.create(accountId, repository).isOrgWritable(orgId)) {
            throw new IllegalAccessException();
        }
        OrgMember newOrgMember = OrgMember.create(
                orgMemberId,
                orgMember.getAccountId(),
                orgMember.getRole());
        repository.updateOrgMember(orgId, newOrgMember);
    }

}
