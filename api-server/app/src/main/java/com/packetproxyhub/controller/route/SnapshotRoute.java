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
import com.packetproxyhub.entity.Snapshot;
import com.packetproxyhub.entity.Snapshots;
import com.packetproxyhub.entity.Id;
import com.packetproxyhub.entity.Ids;
import com.packetproxyhub.interactor.ISnapshotService;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Snapshot Endpoints
// GET    /orgs/111/projects/123/configs/321/snapshots/ (list snapshots)
// GET    /orgs/111/projects/123/configs/321/snapshots/all (get all snapshots)
// POST   /orgs/111/projects/123/configs/321/snapshots/ (create a snapshot)
// GET    /orgs/111/projects/123/configs/321/snapshots/555 (get the snapshot)
// PUT    /orgs/111/projects/123/configs/321/snapshots/555 (edit the snapshot)
// DELETE /orgs/111/projects/123/configs/321/snapshots/555 (delete the snapshot)

public class SnapshotRoute extends Route {
    @Inject
    private ISnapshotService snapshotService;

    @Override
    public boolean getIfMatch(String path, HttpServletRequest request, HttpServletResponse response) {
        // list snapshots
        if (match("/orgs/{orgId}/projects/{projectId}/configs/{configId}/snapshots/", path, (a) -> {
            try {
                Id accountId = getAccountId(request);
                Id orgId = Id.createFromString(a.get("orgId"));
                Id projectId = Id.createFromString(a.get("projectId"));
                Id configId = Id.createFromString(a.get("configId"));

                Ids snapshotId = snapshotService.listSnapshots(accountId, orgId, projectId, configId);

                response.setStatus(HttpServletResponse.SC_OK);
                replyResponseAsJson(response, snapshotId.toJson());

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        })) { return true; }
        // get all snapshots
        if (match ("/orgs/{orgId}/projects/{projectId}/configs/{configId}/snapshots/all", path, (a) -> {
            try {
                Id accountId = getAccountId(request);
                Id orgId = Id.createFromString(a.get("orgId"));
                Id projectId = Id.createFromString(a.get("projectId"));
                Id configId = Id.createFromString(a.get("configId"));

                Snapshots snapshots = snapshotService.getSnapshots(accountId, orgId, projectId, configId);

                response.setStatus(HttpServletResponse.SC_OK);
                replyResponseAsJson(response, snapshots.toJson());

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        })) { return true; }
        // get the snapshot
        if (match ("/orgs/{orgId}/projects/{projectId}/configs/{configId}/snapshots/{snapshotId}", path, (a) -> {
            try {
                Id accountId = getAccountId(request);
                Id orgId = Id.createFromString(a.get("orgId"));
                Id projectId = Id.createFromString(a.get("projectId"));
                Id configId = Id.createFromString(a.get("configId"));
                Id snapshotId = Id.createFromString(a.get("snapshotId"));

                Snapshot snapshot = snapshotService.getSnapshot(accountId, orgId, projectId, configId, snapshotId);
                if (snapshot == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
                    return;
                }

                response.setStatus(HttpServletResponse.SC_OK);
                replyResponseAsJson(response, snapshot.toJson());

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        })) { return true; }

        return false;
    }

    @Override
    public boolean postIfMatch(String path, HttpServletRequest request, HttpServletResponse response) {
        // create a new config
        if (match ("/orgs/{orgId}/projects/{projectId}/configs/{configId}/snapshots/", path, (a) -> {
            try {
                Id accountId = getAccountId(request);
                Id orgId = Id.createFromString(a.get("orgId"));
                Id projectId = Id.createFromString(a.get("projectId"));
                Id configId = Id.createFromString(a.get("configId"));
                String body = IOUtils.toString(request.getReader());

                Snapshot snapshot = Snapshot.createFromJson(body);
                Id snapshotId = snapshotService.createSnapshot(accountId, orgId, projectId, configId, snapshot);

                response.setStatus(HttpServletResponse.SC_OK);
                replyResponseAsJson(response, snapshotId.toJson());

            } catch (Exception e) {
                e.printStackTrace();
            }
        })) { return true; }

        return false;
    }

    @Override
    public boolean putIfMatch(String path, HttpServletRequest request, HttpServletResponse response) {
        // edit the snapshot
        if (match ("/orgs/{orgId}/projects/{projectId}/configs/{configId}/snapshots/{snapshotId}", path, (a) -> {
            try {
                Id accountId = getAccountId(request);
                Id orgId = Id.createFromString(a.get("orgId"));
                Id projectId = Id.createFromString(a.get("projectId"));
                Id configId = Id.createFromString(a.get("configId"));
                Id snapshotId = Id.createFromString(a.get("snapshotId"));
                String body = IOUtils.toString(request.getReader());

                Snapshot snapshot = Snapshot.createFromJson(body);
                snapshotService.updateSnapshot(accountId, orgId, projectId, configId, snapshotId, snapshot);

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
        if (match ("/orgs/{orgId}/projects/{projectId}/configs/{configId}/snapshots/{snapshotId}", path, (a) -> {
            try {
                Id accountId = getAccountId(request);
                Id orgId = Id.createFromString(a.get("orgId"));
                Id projectId = Id.createFromString(a.get("projectId"));
                Id configId = Id.createFromString(a.get("configId"));
                Id snapshotId = Id.createFromString(a.get("snapshotId"));

                snapshotService.removeSnapshot(accountId, orgId, projectId, configId, snapshotId);

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