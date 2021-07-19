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
import com.packetproxyhub.interactor.IRepository;

public class OrgMemberAccessChecker {

    static public OrgMemberAccessChecker create(Id orgId, IRepository repository) {
        return new OrgMemberAccessChecker(orgId, repository);
    }

    private Id orgId;
    private Ids orgMemberIds;

    private OrgMemberAccessChecker() {
    }

    private OrgMemberAccessChecker(Id orgId, IRepository repository) {
        this.orgId = orgId;
        this.orgMemberIds = repository.listOrgMembersInOrg(orgId);
    }

    public boolean canAccessOrgMember(Id orgMemberId) {
        return orgMemberIds.contains(orgMemberId);
    }

}
