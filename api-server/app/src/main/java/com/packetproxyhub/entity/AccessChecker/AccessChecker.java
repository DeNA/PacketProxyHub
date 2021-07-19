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
package com.packetproxyhub.entity.AccessChecker;

import com.packetproxyhub.entity.Id;
import com.packetproxyhub.entity.Ids;
import com.packetproxyhub.entity.OrgMember;
import com.packetproxyhub.entity.OrgMembers;
import com.packetproxyhub.interactor.IRepository;

import java.util.HashMap;
import java.util.Map;

public class AccessChecker {

    static public AccessChecker create(Id accountId, IRepository repository) {
        return new AccessChecker(accountId, repository);
    }

    private Id accountId;
    private IRepository repository;
    private Map<Id, OrgProjectConfigAccessChecker> readableProjectMap = new HashMap<>();
    private Map<Id, OrgProjectConfigAccessChecker> writableProjectMap = new HashMap<>();
    private Map<Id, OrgMemberAccessChecker> readableMemberMap = new HashMap<>();
    private Map<Id, OrgMemberAccessChecker> writableMemberMap = new HashMap<>();

    private AccessChecker() {
    }

    private AccessChecker(Id accountId, IRepository repository) {
        this.accountId = accountId;
        this.repository = repository;

        for (Id orgId : listReadableOrgs(accountId).set()) {
            readableProjectMap.put(orgId, OrgProjectConfigAccessChecker.create(orgId, repository));
            readableMemberMap.put(orgId, OrgMemberAccessChecker.create(orgId, repository));
        }

        for (Id orgId : listWritableOrgs(accountId).set()) {
            writableProjectMap.put(orgId, OrgProjectConfigAccessChecker.create(orgId, repository));
            writableMemberMap.put(orgId, OrgMemberAccessChecker.create(orgId, repository));
        }
    }

    private Ids listReadableOrgs(Id accountId) {
        OrgMembers orgMembers = repository.getOrgMembersFromAccount(accountId);
        return repository.listOrgFromOrgMembers(orgMembers.toIds());
    }

    private Ids listWritableOrgs(Id accountId) {
        OrgMembers orgMembers = repository.getOrgMembersFromAccount(accountId);
        OrgMembers writableOrgMembers = orgMembers.filterBy(OrgMember.Role.Owner);
        return repository.listOrgFromOrgMembers(writableOrgMembers.toIds());
    }

    public boolean isOrgReadable(Id orgId) {
        return readableMemberMap.containsKey(orgId);
    }

    public boolean isProjectReadable(Id orgId, Id projectId) {
        if (!isOrgReadable(orgId)) {
            return false;
        }
        return readableProjectMap.get(orgId).canAccessProject(projectId);
    }

    public boolean isConfigReadable(Id orgId, Id projectId, Id configId) {
        if (!isOrgReadable(orgId)) {
            return false;
        }
        return readableProjectMap.get(orgId).canAccessConfig(projectId, configId);
    }

    public boolean isOrgWritable(Id orgId) {
        return writableMemberMap.containsKey(orgId);
    }

    public boolean isProjectWritable(Id orgId, Id projectId) {
        if (!isOrgWritable(orgId)) {
            return false;
        }
        return writableProjectMap.get(orgId).canAccessProject(projectId);
    }

    public boolean isConfigWritable(Id orgId, Id projectId, Id configId) {
        if (!isOrgWritable(orgId)) {
            return false;
        }
        return writableProjectMap.get(orgId).canAccessConfig(projectId, configId);
    }

}
