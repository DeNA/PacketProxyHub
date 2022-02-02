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
import com.packetproxyhub.entity.Id;
import com.packetproxyhub.interactor.IStorageService;
import org.apache.ibatis.exceptions.PersistenceException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.sql.SQLException;

// Storage Endpoints
// POST   /orgs/111/storages/    (upload object)
// GET    /orgs/111/storages/222 (download the object)
// DELETE /orgs/111/storages/222 (delete the object)

// POST   /orgs/111/storages/multipart/ (upload empty data & get a multipart fileId)
// POST   /orgs/111/storages/multipart/222&action=upload&part=1 (upload the part of the file)
// POST   /orgs/111/storages/multipart/222&action=finalize (finalize all uploaded parts of file)

public class StorageRoute extends Route {
    @Inject
    private IStorageService storageService;

    @Override
    public boolean getIfMatch(String path, HttpServletRequest request, HttpServletResponse response) {
        // get the object
        if (match("/orgs/{orgId}/storages/{storageId}", path, (a) -> {
            try {
                Id myAccountId = getAccountId(request);
                Id orgId = Id.createFromString(a.get("orgId"));
                Id storageId = Id.createFromString(a.get("storageId"));

                InputStream in = storageService.get(myAccountId, orgId, storageId);
                if (in == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/octet-stream");
                String filename = request.getParameter("filename");
                if (filename != null) {
                    response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", filename));
                }
                replyResponseAsBinary(response, in);

            } catch (IllegalAccessException e) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                e.printStackTrace();
            }
        })) { return true; }

        return false;
    }

    @Override
    public boolean postIfMatch(String path, HttpServletRequest request, HttpServletResponse response) {
        // post a new object
        if (match("/orgs/{orgId}/storages/", path, (a) -> {
            try {
                Id accountId = getAccountId(request);
                Id orgId = Id.createFromString(a.get("orgId"));

                Id storageId = storageService.put(accountId, orgId, request.getInputStream());

                response.setStatus(HttpServletResponse.SC_OK);
                replyResponseAsJson(response, storageId.toJson());


            } catch (IllegalAccessException e) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                e.printStackTrace();
            }
        })) { return true; }
        // create a new object
        if (match("/orgs/{orgId}/storages/multipart/", path, (a) -> {
            try {
                Id accountId = getAccountId(request);
                Id orgId = Id.createFromString(a.get("orgId"));

                Id storageId = storageService.generateId(accountId, orgId);

                response.setStatus(HttpServletResponse.SC_OK);
                replyResponseAsJson(response, storageId.toJson());


            } catch (IllegalAccessException e) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                e.printStackTrace();
            }
        })) { return true; }
        // multipart upload
        if (match("/orgs/{orgId}/storages/multipart/{multipartId}", path, (a) -> {
            try {
                Id accountId = getAccountId(request);
                Id orgId = Id.createFromString(a.get("orgId"));
                Id multipartId = Id.createFromString(a.get("multipartId"));

                String action = request.getParameter("action");

                if (action.equals("upload")) {
                    String partNumber = request.getParameter("part");
                    storageService.putPart(accountId, orgId, multipartId, Integer.parseInt(partNumber), request.getInputStream());
                    response.setStatus(HttpServletResponse.SC_OK);
                    replyResponseAsJson(response, "{\"status\":\"ok\"}");
                    return;

                } else if (action.equals("finalize")) {
                    Id mergedId = storageService.mergeParts(accountId, orgId, multipartId);
                    response.setStatus(HttpServletResponse.SC_OK);
                    replyResponseAsJson(response, mergedId.toJson());
                    return;
                }

                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);


            } catch (IllegalAccessException e) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                e.printStackTrace();
            }
        })) { return true; }

        return false;
    }

    @Override
    public boolean deleteIfMatch(String path, HttpServletRequest request, HttpServletResponse response) {
        // delete the object
        if (match("/orgs/{orgId}/storages/{storageId}", path, (a) -> {
            try {
                Id accountId = getAccountId(request);
                Id orgId = Id.createFromString(a.get("orgId"));
                Id storageId = Id.createFromString(a.get("storageId"));

                storageService.remove(accountId, orgId, storageId);
                response.setStatus(HttpServletResponse.SC_OK);
                replyResponseAsJson(response, "{\"status\":\"ok\"}");


            } catch (IllegalAccessException e) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                e.printStackTrace();
            }
        })) { return true; }

        return false;
    }
}