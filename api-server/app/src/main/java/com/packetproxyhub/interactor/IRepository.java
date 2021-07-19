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

public interface IRepository {

    Ids listAccounts();
    Account getAccount(Id accountId);
    Account getAccount(Mail mail);
    Id insertAccount(Account account);
    void updateAccount(Account account);
    void deleteAccount(Id accountId);
    Accounts searchAccount(String nameKey);

    Org getOrg(Id orgId);
    Org getOrg(Name orgName);
    Id insertOrg(Org org);
    void updateOrg(Org org);
    void deleteOrg(Id orgId);

    Ids listOrgFromOrgMembers(Ids orgMembers);
    Ids listOrgMembersInOrg(Id orgId);
    OrgMember getOrgMember(Id orgMemberId);
    OrgMembers getOrgMembersFromAccount(Id accountId);
    void updateOrgMember(Id orgId, OrgMember orgMember);
    Id insertOrgMemberToOrg(Id orgId, OrgMember orgMember);
    void removeOrgMemberFromOrg(Id orgId, Id orgMemberId);

    Ids listProjectsInOrg(Id orgId);
    Project getProject(Id projectId);
    void updateProject(Id orgId, Project project);
    Id insertProjectToOrg(Id orgId, Project project);
    void removeProjectFromOrg(Id orgId, Id projectId);

    Ids listConfigsInProject(Id projectId);
    Config getConfig(Id configId);
    Id insertConfigToProject(Id projectId, Config config);
    void removeConfigFromProject(Id projectId, Id configId);
    void updateConfig(Id projectId, Config config);

}
