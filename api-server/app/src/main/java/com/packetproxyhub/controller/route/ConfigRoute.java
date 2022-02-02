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
import com.packetproxyhub.entity.Config;
import com.packetproxyhub.entity.Configs;
import com.packetproxyhub.entity.Id;
import com.packetproxyhub.entity.Ids;
import com.packetproxyhub.interactor.IConfigService;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Project Configs Endpoints
// GET    /orgs/111/projects/123/configs (list configs)
// POST   /orgs/111/projects/123/configs/ (create a config)
// GET    /orgs/111/projects/123/configs/321 (get the config)
// GET    /orgs/111/projects/123/configs/all (get all configs)
// PUT    /orgs/111/projects/123/configs/321 (edit the config)
// DELETE /orgs/111/projects/123/configs/321 (delete the config)

public class ConfigRoute extends Route {
    @Inject
    private IConfigService configService;

    @Override
    public boolean getIfMatch(String path, HttpServletRequest request, HttpServletResponse response) {
        // list configs
        if (match("/orgs/{orgId}/projects/{projectId}/configs", path, (a) -> {
            try {
                Id accountId = getAccountId(request);
                Id orgId = Id.createFromString(a.get("orgId"));
                Id projectId = Id.createFromString(a.get("projectId"));

                Ids configIds = configService.listConfigs(accountId, orgId, projectId);

                response.setStatus(HttpServletResponse.SC_OK);
                replyResponseAsJson(response, configIds.toJson());

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                e.printStackTrace();
            }
        })) { return true; }
        // get all configs
        if (match ("/orgs/{orgId}/projects/{projectId}/configs/all", path, (a) -> {
            try {
                Id accountId = getAccountId(request);
                Id orgId = Id.createFromString(a.get("orgId"));
                Id projectId = Id.createFromString(a.get("projectId"));

                Configs configs = configService.getConfigs(accountId, orgId, projectId);

                response.setStatus(HttpServletResponse.SC_OK);
                replyResponseAsJson(response, configs.toJson());

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                e.printStackTrace();
            }
        })) { return true; }
        // get the config
        if (match ("/orgs/{orgId}/projects/{projectId}/configs/{configId}", path, (a) -> {
            try {
                Id accountId = getAccountId(request);
                Id orgId = Id.createFromString(a.get("orgId"));
                Id projectId = Id.createFromString(a.get("projectId"));
                Id configId = Id.createFromString(a.get("configId"));

                Config config = configService.getConfig(accountId, orgId, projectId, configId);
                if (config == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
                    return;
                }

                response.setStatus(HttpServletResponse.SC_OK);
                replyResponseAsJson(response, config.toJson());

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                e.printStackTrace();
            }
        })) { return true; }

        return false;
    }

    @Override
    public boolean postIfMatch(String path, HttpServletRequest request, HttpServletResponse response) {
        // create a new config
        if (match ("/orgs/{orgId}/projects/{projectId}/configs/", path, (a) -> {
            try {
                Id accountId = getAccountId(request);
                Id orgId = Id.createFromString(a.get("orgId"));
                Id projectId = Id.createFromString(a.get("projectId"));
                String body = IOUtils.toString(request.getReader());

                Config config = Config.createFromJson(body);
                Id configId = configService.createConfig(accountId, orgId, projectId, config);

                response.setStatus(HttpServletResponse.SC_OK);
                replyResponseAsJson(response, configId.toJson());

            } catch (Exception e) {
                e.printStackTrace();
            }
        })) { return true; }

        return false;
    }

    @Override
    public boolean putIfMatch(String path, HttpServletRequest request, HttpServletResponse response) {
        // edit the config
        if (match ("/orgs/{orgId}/projects/{projectId}/configs/{configId}", path, (a) -> {
            try {
                Id accountId = getAccountId(request);
                Id orgId = Id.createFromString(a.get("orgId"));
                Id projectId = Id.createFromString(a.get("projectId"));
                Id configId = Id.createFromString(a.get("configId"));
                String body = IOUtils.toString(request.getReader());

                Config config = Config.createFromJson(body);
                configService.updateConfig(accountId, orgId, projectId, configId, config);

                response.setStatus(HttpServletResponse.SC_OK);
                replyResponseAsJson(response, "{\"status\":\"ok\"}");

            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        })) { return true; }

        return false;
    }

    @Override
    public boolean deleteIfMatch(String path, HttpServletRequest request, HttpServletResponse response) {
        // delete the config
        if (match ("/orgs/{orgId}/projects/{projectId}/configs/{configId}", path, (a) -> {
            try {
                Id accountId = getAccountId(request);
                Id orgId = Id.createFromString(a.get("orgId"));
                Id projectId = Id.createFromString(a.get("projectId"));
                Id configId = Id.createFromString(a.get("configId"));

                configService.removeConfig(accountId, orgId, projectId, configId);

                response.setStatus(HttpServletResponse.SC_OK);
                replyResponseAsJson(response, "{\"status\":\"ok\"}");

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                e.printStackTrace();
            }
        })) { return true; }

        return false;
    }
}
