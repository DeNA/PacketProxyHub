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

public interface IOrgMemberService {
    Ids listOrgMembers(Id accountId, Id orgId) throws Exception;
    Accounts searchAccount(Id accountId, Id orgId, String nameKey) throws Exception;
    OrgMember getOrgMember(Id accountId, Id orgId, Id orgMemberId) throws Exception;
    OrgMembers getOrgMembers(Id accountId, Id orgId) throws Exception;
    Id createOrgMember(Id accountId, Id orgId, OrgMember orgMember) throws Exception;
    void updateOrgMember(Id accountId, Id orgId, Id orgMemberId, OrgMember orgMember) throws Exception;
    void removeOrgMember(Id accountId, Id orgId, Id orgMemberId) throws Exception;
}


