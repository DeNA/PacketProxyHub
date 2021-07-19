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

import com.packetproxyhub.entity.Id;
import com.packetproxyhub.entity.Ids;
import com.packetproxyhub.entity.Org;
import com.packetproxyhub.entity.Orgs;

public interface IOrgService {
    Ids listReadableOrgIds(Id accountId) throws Exception;
    Ids listWritableOrgIds(Id accountId) throws Exception;
    Orgs listReadableOrgs(Id accountId) throws Exception;
    Orgs listWritableOrgs(Id accountId) throws Exception;
    Org getOrg(Id accountId, Id orgId) throws Exception;
    Id createOrg(Id accountId, Org org) throws Exception;
    void updateOrg(Id accountId, Id orgId, Org org) throws Exception;
    void removeOrg(Id accountId, Id orgId) throws Exception;
}


