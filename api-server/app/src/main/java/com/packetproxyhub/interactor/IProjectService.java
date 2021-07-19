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
import com.packetproxyhub.entity.Project;
import com.packetproxyhub.entity.Projects;

public interface IProjectService {
    Ids listProjectIds(Id accountId, Id orgId) throws Exception;
    Projects getProjects(Id accountId, Id orgId) throws Exception;
    Project getProject(Id accountId, Id orgId, Id projectId) throws Exception;
    Id createProject(Id accountId, Id orgId, Project project) throws Exception;
    void updateProject(Id accountId, Id orgId, Id projectId, Project project) throws Exception;
    void removeProject(Id accountId, Id orgId, Id projectId) throws Exception;
}
