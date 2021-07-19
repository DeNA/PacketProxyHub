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
import com.packetproxyhub.interactor.IOrgMemberService;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Org OrgMembers Endpoints
// GET    /orgs/123/members (list orgMembers)
// GET    /orgs/123/members/search?nameKey=xxx (search orgMembers by key)
// POST   /orgs/123/members/ (create a orgMember)
// GET    /orgs/123/members/321 (get the orgMember)
// GET    /orgs/123/members/all (get all orgMembers)
// PUT    /orgs/123/members/321 (edit the orgMember)
// DELETE /orgs/123/members/321 (delete the orgMember)

public class OrgMemberRoute extends Route {
    @Inject
    private IOrgMemberService orgMemberService;

    @Override
    public boolean getIfMatch(String path, HttpServletRequest request, HttpServletResponse response) {
        // list orgMembers
        if (match("/orgs/{orgId}/members", path, (a) -> {
            try {
                Id accountId = getAccountId(request);
                Id orgId = Id.createFromString(a.get("orgId"));

                Ids orgMemberIds = orgMemberService.listOrgMembers(accountId, orgId);
                replyResponseAsJson(response, orgMemberIds.toJson());

            } catch (IllegalAccessException e) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                e.printStackTrace();
            }
        })) { return true; }
        // get all orgMembers
        if (match ("/orgs/{orgId}/members/all", path, (a) -> {
            try {
                Id accountId = getAccountId(request);
                Id orgId = Id.createFromString(a.get("orgId"));

                OrgMembers orgMembers = orgMemberService.getOrgMembers(accountId, orgId);

                response.setStatus(HttpServletResponse.SC_OK);
                replyResponseAsJson(response, orgMembers.toJson());

            } catch (IllegalAccessException e) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                e.printStackTrace();
            }
        })) { return true; }
        // get the orgMember
        if (match ("/orgs/{orgId}/members/{orgMemberId}", path, (a) -> {
            try {
                Id accountId = getAccountId(request);
                Id orgId = Id.createFromString(a.get("orgId"));
                Id orgMemberId = Id.createFromString(a.get("orgMemberId"));

                OrgMember orgMember = orgMemberService.getOrgMember(accountId, orgId, orgMemberId);
                if (orgMember == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
                    return;
                }
                replyResponseAsJson(response, orgMember.toJson());

            } catch (IllegalAccessException e) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                e.printStackTrace();
            }
        })) { return true; }
        // search by name
        if (match ("/orgs/{orgId}/members/search", path, (a) -> {
            try {
                Id accountId = getAccountId(request);
                Id orgId = Id.createFromString(a.get("orgId"));
                String nameKey = request.getParameter("nameKey");
                Accounts accounts = orgMemberService.searchAccount(accountId, orgId, nameKey);

                response.setStatus(HttpServletResponse.SC_OK);
                replyResponseAsJson(response, accounts.toJson());

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
        // create a new orgMember
        if (match ("/orgs/{orgId}/members/", path, (a) -> {
            try {
                Id accountId = getAccountId(request);
                Id orgId = Id.createFromString(a.get("orgId"));
                String body = IOUtils.toString(request.getReader());

                OrgMember orgMember = OrgMember.createFromJson(body);
                Id orgMemberId = orgMemberService.createOrgMember(accountId, orgId, orgMember);
                replyResponseAsJson(response, orgMemberId.toJson());

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
    public boolean putIfMatch(String path, HttpServletRequest request, HttpServletResponse response) {
        // edit the orgMember
        if (match ("/orgs/{orgId}/members/{orgMemberId}", path, (a) -> {
            try {
                Id accountId = getAccountId(request);
                Id orgId = Id.createFromString(a.get("orgId"));
                Id orgMemberId = Id.createFromString(a.get("orgMemberId"));
                String body = IOUtils.toString(request.getReader());

                OrgMember orgMember = OrgMember.createFromJson(body);
                orgMemberService.updateOrgMember(accountId, orgId, orgMemberId, orgMember);

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

    @Override
    public boolean deleteIfMatch(String path, HttpServletRequest request, HttpServletResponse response) {
        // delete the orgMember
        if (match ("/orgs/{orgId}/members/{orgMemberId}", path, (a) -> {
            try {
                Id accountId = getAccountId(request);
                Id orgId = Id.createFromString(a.get("orgId"));
                Id orgMemberId = Id.createFromString(a.get("orgMemberId"));

                orgMemberService.removeOrgMember(accountId, orgId, orgMemberId);

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
