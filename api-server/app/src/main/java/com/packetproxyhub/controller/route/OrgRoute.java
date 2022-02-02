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
import com.packetproxyhub.entity.Ids;
import com.packetproxyhub.entity.Org;
import com.packetproxyhub.entity.Orgs;
import com.packetproxyhub.interactor.IOrgService;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Orgs Endpoints
// GET    /orgs (list orgs)
// POST   /orgs/ (create a org)
// GET    /orgs/111 (get the org)
// GET    /orgs/all (get all orgs)
// PUT    /orgs/111 (edit the org)
// DELETE /orgs/111 (delete the org)

public class OrgRoute extends Route {
    @Inject
    private IOrgService orgService;

    @Override
    public boolean getIfMatch(String path, HttpServletRequest request, HttpServletResponse response) {
        // list orgs
        if (match("/orgs", path, (a) -> {
            try {
                Id myAccountId = getAccountId(request);
                Ids orgIds = orgService.listReadableOrgIds(myAccountId);

                response.setStatus(HttpServletResponse.SC_OK);
                replyResponseAsJson(response, orgIds.toJson());

            } catch (IllegalAccessException e) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                LoginRoute.resetCookie(response);

            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        })) { return true; }
        // get all orgs
        if (match ("/orgs/all", path, (a) -> {
            try {
                Id myAccountId = getAccountId(request);

                Orgs orgs = orgService.listReadableOrgs(myAccountId);

                response.setStatus(HttpServletResponse.SC_OK);
                replyResponseAsJson(response, orgs.toJson());

            } catch (IllegalAccessException e) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                LoginRoute.resetCookie(response);

            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        })) { return true; }
        // get a org
        if (match ("/orgs/{orgId}", path, (a) -> {
            try {
                Id myAccountId = getAccountId(request);
                Id orgId = Id.createFromString(a.get("orgId"));

                Org org = orgService.getOrg(myAccountId, orgId);
                if (org == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }

                response.setStatus(HttpServletResponse.SC_OK);
                replyResponseAsJson(response, org.toJson());

            } catch (IllegalAccessException e) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                LoginRoute.resetCookie(response);

            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        })) { return true; }

        return false;
    }

    @Override
    public boolean postIfMatch(String path, HttpServletRequest request, HttpServletResponse response) {
        // create a new org
        if (match ("/orgs/", path, (a) -> {
            try {
                Id accountId = getAccountId(request);
                String body = IOUtils.toString(request.getReader());

                Org org = Org.createFromJson(body);
                Id orgId = orgService.createOrg(accountId, org);

                response.setStatus(HttpServletResponse.SC_OK);
                replyResponseAsJson(response, orgId.toJson());

            } catch (IllegalAccessException e) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                LoginRoute.resetCookie(response);

            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        })) { return true; }

        return false;
    }

    @Override
    public boolean putIfMatch(String path, HttpServletRequest request, HttpServletResponse response) {
        // edit the org
        if (match ("/orgs/{orgId}", path, (a) -> {
            try {
                Id myAccountId = getAccountId(request);
                Id orgId = Id.createFromString(a.get("orgId"));
                String body = IOUtils.toString(request.getReader());

                Org putOrg = Org.createFromJson(body);
                orgService.updateOrg(myAccountId, orgId, putOrg);

                response.setStatus(HttpServletResponse.SC_OK);
                replyResponseAsJson(response, "{\"status\":\"ok\"}");

            } catch (IllegalAccessException e) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                LoginRoute.resetCookie(response);

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                e.printStackTrace();
            }
        })) { return true; }

        return false;
    }

    @Override
    public boolean deleteIfMatch(String path, HttpServletRequest request, HttpServletResponse response) {
        // delete the org
        if (match ("/orgs/{orgId}", path, (a) -> {
            try {
                Id myAccountId = getAccountId(request);
                Id orgId = Id.createFromString(a.get("orgId"));

                orgService.removeOrg(myAccountId, orgId);

                response.setStatus(HttpServletResponse.SC_OK);
                replyResponseAsJson(response, "{\"status\":\"ok\"}");

            } catch (IllegalAccessException e) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                LoginRoute.resetCookie(response);

            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        })) { return true; }

        return false;
    }

}
