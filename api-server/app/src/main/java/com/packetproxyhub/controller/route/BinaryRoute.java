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
import com.packetproxyhub.entity.Binaries;
import com.packetproxyhub.entity.Binary;
import com.packetproxyhub.entity.Id;
import com.packetproxyhub.entity.Ids;
import com.packetproxyhub.interactor.IBinaryService;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Binary Endpoints
// GET    /orgs/111/projects/123/configs/321/binaries/ (list binaries)
// GET    /orgs/111/projects/123/configs/321/binaries/all (get all binaries)
// POST   /orgs/111/projects/123/configs/321/binaries/ (create a binary)
// GET    /orgs/111/projects/123/configs/321/binaries/555 (get the binary)
// PUT    /orgs/111/projects/123/configs/321/binaries/555 (edit the binary)
// DELETE /orgs/111/projects/123/configs/321/binaries/555 (delete the binary)

public class BinaryRoute extends Route {
    @Inject
    private IBinaryService binaryService;

    @Override
    public boolean getIfMatch(String path, HttpServletRequest request, HttpServletResponse response) {
        // list binaries
        if (match("/orgs/{orgId}/projects/{projectId}/configs/{configId}/binaries/", path, (a) -> {
            try {
                Id accountId = getAccountId(request);
                Id orgId = Id.createFromString(a.get("orgId"));
                Id projectId = Id.createFromString(a.get("projectId"));
                Id configId = Id.createFromString(a.get("configId"));

                Ids binaryId = binaryService.listBinaries(accountId, orgId, projectId, configId);

                response.setStatus(HttpServletResponse.SC_OK);
                replyResponseAsJson(response, binaryId.toJson());

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        })) { return true; }
        // get all binaries
        if (match ("/orgs/{orgId}/projects/{projectId}/configs/{configId}/binaries/all", path, (a) -> {
            try {
                Id accountId = getAccountId(request);
                Id orgId = Id.createFromString(a.get("orgId"));
                Id projectId = Id.createFromString(a.get("projectId"));
                Id configId = Id.createFromString(a.get("configId"));

                Binaries binaries = binaryService.getBinaries(accountId, orgId, projectId, configId);

                response.setStatus(HttpServletResponse.SC_OK);
                replyResponseAsJson(response, binaries.toJson());

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        })) { return true; }
        // get the binary
        if (match ("/orgs/{orgId}/projects/{projectId}/configs/{configId}/binaries/{binaryId}", path, (a) -> {
            try {
                Id accountId = getAccountId(request);
                Id orgId = Id.createFromString(a.get("orgId"));
                Id projectId = Id.createFromString(a.get("projectId"));
                Id configId = Id.createFromString(a.get("configId"));
                Id binaryId = Id.createFromString(a.get("binaryId"));

                Binary binary = binaryService.getBinary(accountId, orgId, projectId, configId, binaryId);
                if (binary == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
                    return;
                }

                response.setStatus(HttpServletResponse.SC_OK);
                replyResponseAsJson(response, binary.toJson());

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        })) { return true; }

        return false;
    }

    @Override
    public boolean postIfMatch(String path, HttpServletRequest request, HttpServletResponse response) {
        // create a new config
        if (match ("/orgs/{orgId}/projects/{projectId}/configs/{configId}/binaries/", path, (a) -> {
            try {
                Id accountId = getAccountId(request);
                Id orgId = Id.createFromString(a.get("orgId"));
                Id projectId = Id.createFromString(a.get("projectId"));
                Id configId = Id.createFromString(a.get("configId"));
                String body = IOUtils.toString(request.getReader());

                Binary binary = Binary.createFromJson(body);
                Id binaryId = binaryService.createBinary(accountId, orgId, projectId, configId, binary);

                response.setStatus(HttpServletResponse.SC_OK);
                replyResponseAsJson(response, binaryId.toJson());

            } catch (Exception e) {
                e.printStackTrace();
            }
        })) { return true; }

        return false;
    }

    @Override
    public boolean putIfMatch(String path, HttpServletRequest request, HttpServletResponse response) {
        // edit the binary
        if (match ("/orgs/{orgId}/projects/{projectId}/configs/{configId}/binaries/{binaryId}", path, (a) -> {
            try {
                Id accountId = getAccountId(request);
                Id orgId = Id.createFromString(a.get("orgId"));
                Id projectId = Id.createFromString(a.get("projectId"));
                Id configId = Id.createFromString(a.get("configId"));
                Id binaryId = Id.createFromString(a.get("binaryId"));
                String body = IOUtils.toString(request.getReader());

                Binary binary = Binary.createFromJson(body);
                binaryService.updateBinary(accountId, orgId, projectId, configId, binaryId, binary);

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
        if (match ("/orgs/{orgId}/projects/{projectId}/configs/{configId}/binaries/{binaryId}", path, (a) -> {
            try {
                Id accountId = getAccountId(request);
                Id orgId = Id.createFromString(a.get("orgId"));
                Id projectId = Id.createFromString(a.get("projectId"));
                Id configId = Id.createFromString(a.get("configId"));
                Id binaryId = Id.createFromString(a.get("binaryId"));

                binaryService.removeBinary(accountId, orgId, projectId, configId, binaryId);

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