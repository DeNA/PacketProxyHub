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
import com.packetproxyhub.entity.Resource;
import com.packetproxyhub.interactor.IResourceService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

// GET /all (get all resources)

public class ResourceRoute extends Route {
    @Inject
    private IResourceService resourceService;

    @Override
    public boolean getIfMatch(String path, HttpServletRequest request, HttpServletResponse response) {
        // list orgMembers
        if (match("/all", path, (a) -> {
            try {
                Id myAccountId = getAccountId(request);

                Resource resource = resourceService.getAllResource(myAccountId);

                response.setStatus(HttpServletResponse.SC_OK);
                replyResponseAsJson(response, resource.toJson());

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
}