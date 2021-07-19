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
import com.google.inject.Injector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

public class Routes {

    private List<Route> routeMap = new ArrayList<>();

    @Inject
    public Routes(Injector injector) {
        routeMap.add(injector.getInstance(LoginRoute.class));
        routeMap.add(injector.getInstance(AccountRoute.class));
        routeMap.add(injector.getInstance(ProjectRoute.class));
        routeMap.add(injector.getInstance(ConfigRoute.class));
        routeMap.add(injector.getInstance(OrgRoute.class));
        routeMap.add(injector.getInstance(OrgMemberRoute.class));
        routeMap.add(injector.getInstance(ResourceRoute.class));
    }

    public void execGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
        for (Route route: routeMap) {
            if (route.getIfMatch(request.getPathInfo(), request, response)) {
                return;
            }
        }
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
    public void execPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
        for (Route route: routeMap) {
            if (route.postIfMatch(request.getPathInfo(), request, response)) {
                return;
            }
        }
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
    public void execPut(HttpServletRequest request, HttpServletResponse response) throws Exception {
        for (Route route: routeMap) {
            if (route.putIfMatch(request.getPathInfo(), request, response)) {
                return;
            }
        }
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
    public void execDelete(HttpServletRequest request, HttpServletResponse response) throws Exception {
        for (Route route: routeMap) {
            if (route.deleteIfMatch(request.getPathInfo(), request, response)) {
                return;
            }
        }
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

}
