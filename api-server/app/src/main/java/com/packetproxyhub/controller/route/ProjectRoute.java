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
package com.packetproxyhub.controller.route;

import com.google.inject.Inject;
import com.packetproxyhub.entity.*;
import com.packetproxyhub.interactor.IOrgService;
import com.packetproxyhub.interactor.IProjectService;
import com.packetproxyhub.interactor.ProjectService;
import org.apache.commons.io.IOUtils;
import org.apache.ibatis.exceptions.PersistenceException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.sql.SQLInvalidAuthorizationSpecException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

// Projects Endpoints
// GET    /orgs/111/projects (list projects)
// POST   /orgs/111/projects/ (create a project)
// GET    /orgs/111/projects/123 (get the project)
// GET    /orgs/111/projects/all (get all projects)
// PUT    /orgs/111/projects/123 (edit the project)
// DELETE /orgs/111/projects/123 (delete the project)

public class ProjectRoute extends Route {
    @Inject
    private IProjectService projectService;

    @Override
    public boolean getIfMatch(String path, HttpServletRequest request, HttpServletResponse response) {
        // list projects
        if (match("/orgs/{orgId}/projects", path, (a) -> {
            try {
                Id accountId = getAccountId(request);
                Id orgId = Id.createFromString(a.get("orgId"));

                Ids projectIds = projectService.listProjectIds(accountId, orgId);
                replyResponseAsJson(response, projectIds.toJson());

            } catch (IllegalAccessException e) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                e.printStackTrace();
            }
        })) { return true; }
        // get all projects
        if (match("/orgs/{orgId}/projects/all", path, (a) -> {
            try {
                Id myAccountId = getAccountId(request);
                Id orgId = Id.createFromString(a.get("orgId"));

                Projects projects = projectService.getProjects(myAccountId, orgId);

                response.setStatus(HttpServletResponse.SC_OK);
                replyResponseAsJson(response, projects.toJson());

            } catch (IllegalAccessException e) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                e.printStackTrace();
            }
        })) { return true; }
        // get a project
        if (match("/orgs/{orgId}/projects/{projectId}", path, (a) -> {
            try {
                Id myAccountId = getAccountId(request);
                Id orgId = Id.createFromString(a.get("orgId"));
                Id projectId = Id.createFromString(a.get("projectId"));

                Project project = projectService.getProject(myAccountId, orgId, projectId);
                if (project == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                response.setStatus(HttpServletResponse.SC_OK);
                replyResponseAsJson(response, project.toJson());

            } catch (IllegalAccessException e) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            } catch (SQLException e) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                e.printStackTrace();
            }
        })) { return true; }

        return false;
    }

    @Override
    public boolean postIfMatch(String path, HttpServletRequest request, HttpServletResponse response) {
        // create a new project
        if (match("/orgs/{orgId}/projects/", path, (a) -> {
            try {
                Id accountId = getAccountId(request);
                Id orgId = Id.createFromString(a.get("orgId"));
                String body = IOUtils.toString(request.getReader());

                Project project = Project.createFromJson(body);
                Id projectId = projectService.createProject(accountId, orgId, project);

                response.setStatus(HttpServletResponse.SC_OK);
                replyResponseAsJson(response, projectId.toJson());

            } catch (IllegalAccessException e) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            } catch (SQLException e) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                e.printStackTrace();
            }
        })) { return true; }

        return false;
    }

    @Override
    public boolean putIfMatch(String path, HttpServletRequest request, HttpServletResponse response) {
        // edit the project
        if (match("/orgs/{orgId}/projects/{projectId}", path, (a) -> {
            try {
                Id accountId = getAccountId(request);
                Id orgId = Id.createFromString(a.get("orgId"));
                Id projectId = Id.createFromString(a.get("projectId"));
                String body = IOUtils.toString(request.getReader());

                Project project = Project.createFromJson(body);
                projectService.updateProject(accountId, orgId, projectId, project);

                response.setStatus(HttpServletResponse.SC_OK);
                replyResponseAsJson(response, "{\"status\":\"ok\"}");

            } catch (IllegalAccessException e) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            } catch (SQLException e) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                e.printStackTrace();
            }
        })) { return true; }

        return false;
    }

    @Override
    public boolean deleteIfMatch(String path, HttpServletRequest request, HttpServletResponse response) {
        // delete the project
        if (match("/orgs/{orgId}/projects/{projectId}", path, (a) -> {
            try {
                Id accountId = getAccountId(request);
                Id orgId = Id.createFromString(a.get("orgId"));
                Id projectId = Id.createFromString(a.get("projectId"));

                projectService.removeProject(accountId, orgId, projectId);

                response.setStatus(HttpServletResponse.SC_OK);
                replyResponseAsJson(response, "{\"status\":\"ok\"}");

            } catch (IllegalAccessException e) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            } catch (PersistenceException e) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                e.printStackTrace();
            }
        })) { return true; }

        return false;
    }
}
