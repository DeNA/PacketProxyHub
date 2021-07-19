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
package com.packetproxyhub.repository.sqlite.dao;

import com.packetproxyhub.entity.Id;
import com.packetproxyhub.entity.OrgMember;
import com.packetproxyhub.entity.OrgMembers;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class DaoOrgMember {
    private long dbId;
    private DaoId id;
    private String role;
    private DaoId orgId;
    private DaoId accountId;

    static public DaoOrgMember create(Id orgId, OrgMember orgMember) {
        return new DaoOrgMember(orgId, orgMember);
    }

    static public OrgMembers toOrgMembers(Set<DaoOrgMember> daoOrgMemberSet) {
        OrgMembers orgMembers = OrgMembers.create();
        for (DaoOrgMember daoOrgMember : daoOrgMemberSet) {
            orgMembers.add(daoOrgMember.toOrgMember());
        }
        return orgMembers;
    }

    public DaoOrgMember() {
    }

    private DaoOrgMember(Id orgId, OrgMember orgMember) {
        this.dbId = orgMember.getId().toLong();
        this.orgId = DaoId.create(orgId);
        this.id = DaoId.create(orgMember.getId());
        this.accountId = DaoId.create(orgMember.getAccountId());
        this.role = orgMember.getRole().toString();
    }

    public OrgMember toOrgMember() {
        return OrgMember.create(id.toId(), accountId.toId(), OrgMember.Role.valueOf(role));
    }

}