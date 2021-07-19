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
import com.packetproxyhub.entity.Project;
import com.packetproxyhub.entity.Projects;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class DaoProject {
    private long dbId;
    private DaoId orgId;
    private DaoId id;
    private String name;
    private String description;
    private String content;
    private long updatedAt;

    static public Projects toProjects(Set<DaoProject> daoProjectSet) {
        Projects projects = new Projects();
        for (DaoProject daoProject : daoProjectSet) {
            projects.add(daoProject.toProject());
        }
        return projects;
    }

    public DaoProject() {
    }

    public DaoProject(Id orgId, Project project) {
        this.dbId = project.getId().toLong();
        this.orgId = DaoId.create(orgId);
        this.id = DaoId.create(project.getId());
        this.name = project.getName();
        this.description = project.getDescription();
        this.content = project.getContent();
        this.updatedAt = project.getUpdatedAt();
    }

    public Project toProject() {
        return Project.create(id.toId(), name, description, content, updatedAt);
    }

}