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

import com.packetproxyhub.entity.AccessChecker.AccessChecker;
import com.packetproxyhub.entity.*;

import javax.inject.Inject;
import java.util.Set;
import java.util.stream.Collectors;

public class OrgService implements IOrgService {
    @Inject
    private IRepository repository;

    @Override
    public Ids listReadableOrgIds(Id accountId) throws Exception {
        OrgMembers orgMembers = repository.getOrgMembersFromAccount(accountId);
        return repository.listOrgFromOrgMembers(orgMembers.toIds());
    }

    @Override
    public Ids listWritableOrgIds(Id accountId) throws Exception {
        OrgMembers orgMembers = repository.getOrgMembersFromAccount(accountId);
        OrgMembers writableOrgMembers = orgMembers.filterBy(OrgMember.Role.Owner);
        return repository.listOrgFromOrgMembers(writableOrgMembers.toIds());
    }

    @Override
    public Orgs listReadableOrgs(Id accountId) throws Exception {
        Ids orgIds = listReadableOrgIds(accountId);
        return getOrgs(accountId, orgIds);
    }

    @Override
    public Orgs listWritableOrgs(Id accountId) throws Exception {
        Ids orgIds = listWritableOrgIds(accountId);
        return getOrgs(accountId, orgIds);
    }

    private Orgs getOrgs(Id accountId, Ids orgIds) {
        Set<Org> orgSet = orgIds.set().stream().map(orgId -> repository.getOrg(orgId)).collect(Collectors.toSet());
        return Orgs.createWithInit(orgSet);
    }

    @Override
    public Org getOrg(Id accountId, Id orgId) throws Exception {
        if (!AccessChecker.create(accountId, repository).isOrgReadable(orgId)) {
            throw new IllegalAccessException();
        }
        Org org = repository.getOrg(orgId);
        if (org == null) {
            throw new UnknownError();
        }
        return org;
    }

    @Override
    public Id createOrg(Id accountId, Org org) {
        Id orgId = repository.insertOrg(org);
        repository.insertOrgMemberToOrg(orgId, OrgMember.create(accountId, OrgMember.Role.Owner));
        return orgId;
    }

    @Override
    public void removeOrg(Id accountId , Id orgId) throws Exception {
        if (AccessChecker.create(accountId, repository).isOrgReadable(orgId) == false) {
            throw new IllegalAccessException();
        }
        repository.deleteOrg(orgId);
        // TODO: all resources should be removed from org
    }

    @Override
    public void updateOrg(Id accountId, Id orgId, Org org) throws Exception {
        if (AccessChecker.create(accountId, repository).isOrgWritable(orgId) == false) {
            throw new IllegalAccessException();
        }
        Org newOrg = Org.create(
                orgId,
                org.getName(),
                org.getDescription());
        repository.updateOrg(newOrg);
    }

}
