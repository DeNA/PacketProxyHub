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

import java.util.HashMap;
import java.util.Map;

public class OrgProjectConfigAccessChecker {

    static public OrgProjectConfigAccessChecker create(Id orgId, IRepository repository) {
        return new OrgProjectConfigAccessChecker(orgId, repository);
    }

    private Id orgId;
    private Map<Id, Ids> projectConfigMap = new HashMap<>();

    private OrgProjectConfigAccessChecker() {
    }

    private OrgProjectConfigAccessChecker(Id orgId, IRepository repository) {
        this.orgId = orgId;
        Ids projectIds = repository.listProjectsInOrg(orgId);
        for (Id projectId : projectIds.set()) {
            Ids configIds = repository.listConfigsInProject(projectId);
            projectConfigMap.put(projectId, configIds);
        }
    }

    public boolean canAccessProject(Id projectId) {
        return projectConfigMap.keySet().contains(projectId);
    }

    public boolean canAccessConfig(Id projectId, Id configId) {
        if (canAccessProject(projectId) == false) {
            return false;
        }
        return projectConfigMap.get(projectId).contains(configId);
    }

}
