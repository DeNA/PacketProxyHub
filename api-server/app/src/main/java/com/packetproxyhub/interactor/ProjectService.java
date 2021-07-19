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
import com.packetproxyhub.entity.Id;
import com.packetproxyhub.entity.Ids;
import com.packetproxyhub.entity.Project;
import com.packetproxyhub.entity.Projects;

import javax.inject.Inject;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ProjectService implements IProjectService {
    @Inject
    private IRepository repository;

    @Override
    public Ids listProjectIds(Id accountId, Id orgId) throws Exception {
        if (!AccessChecker.create(accountId, repository).isOrgReadable(orgId)) {
            throw new IllegalAccessException();
        }
        return repository.listProjectsInOrg(orgId);
    }

    @Override
    public Projects getProjects(Id accountId, Id orgId) throws Exception {
        Ids projectIds = repository.listProjectsInOrg(orgId);
        Set<Project> projectSet = projectIds.set().stream().map(projectId -> repository.getProject(projectId)).collect(Collectors.toSet());
        return Projects.createWithInit(projectSet);
    }

    @Override
    public Project getProject(Id accountId, Id orgId, Id projectId) throws Exception {
        if (!AccessChecker.create(accountId, repository).isProjectReadable(orgId, projectId)) {
            throw new IllegalAccessException();
        }
        return repository.getProject(projectId);
    }

    @Override
    public Id createProject(Id accountId, Id orgId, Project project) throws Exception {
        if (!AccessChecker.create(accountId, repository).isOrgReadable(orgId)) {
            throw new IllegalAccessException();
        }
        return repository.insertProjectToOrg(orgId, project);
    }

    @Override
    public void removeProject(Id accountId, Id orgId, Id projectId) throws Exception {
        if (!AccessChecker.create(accountId, repository).isProjectReadable(orgId, projectId)) {
            throw new IllegalAccessException();
        }
        repository.removeProjectFromOrg(orgId, projectId);
    }

    @Override
    public void updateProject(Id accountId, Id orgId, Id projectId, Project project) throws Exception {
        if (!AccessChecker.create(accountId, repository).isProjectReadable(orgId, projectId)) {
            throw new IllegalAccessException();
        }
        Project newProject = Project.create(
                projectId,
                project.getName(),
                project.getDescription(),
                project.getContent());
        repository.updateProject(orgId, newProject);
    }

}
