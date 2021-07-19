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
import com.packetproxyhub.application.App;
import com.packetproxyhub.entity.*;
import com.packetproxyhub.interactor.IAccountService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Login Endpoints
// GET   /logout
// POST  /login/debug (for development)
// POST  /login/okta

public class LoginRoute extends Route {
    @Inject
    private IAccountService accountService;

    private void setSessionKeyToCookie(HttpServletResponse response, SessionKey sessionKey) {
        Cookie cookie = new Cookie("packetproxyhub_session", sessionKey.toSessonKeyString());
        cookie.setMaxAge(86400); /* 1 day */
        cookie.setPath("/");
        if (App.env.isProductionEnv()) {
            cookie.setSecure(true);
        }
        response.addCookie(cookie);
    }

    private void resetCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("packetproxyhub_session", "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        if (App.env.isProductionEnv()) {
            cookie.setSecure(true);
        }
        response.addCookie(cookie);
    }

    @Override
    public boolean getIfMatch(String path, HttpServletRequest request, HttpServletResponse response) {
        if (match("/logout", path, (a) -> {
            try {
                response.setStatus(HttpServletResponse.SC_OK);
                resetCookie(response);

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        })) { return true; }
        return false;
    }

    @Override
    public boolean postIfMatch(String path, HttpServletRequest request, HttpServletResponse response) {
        if (App.env.isDevelopmentEnv()) {
            if (match("/login/debug", path, (a) -> {
                try {
                    Name name = Name.create(request.getParameter("name"));
                    Mail mail = Mail.create(request.getParameter("mail"));

                    SessionKey sessionKey = accountService.login(name, mail);
                    setSessionKeyToCookie(response, sessionKey);

                    response.setStatus(HttpServletResponse.SC_OK);

                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    e.printStackTrace();
                }
            })) return true;
        }
        if (App.settings.useGoogleLogin()) {
            if (match("/login/google", path, (a) -> {
                try {
                    String idTokenString = request.getParameter("id_token");
                    GoogleLogin google = new GoogleLogin(idTokenString);
                    if (!google.verify()) {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        return;
                    }
                    Mail mail = google.mail();
                    Name name = google.name();

                    SessionKey sessionKey = accountService.login(name, mail);
                    setSessionKeyToCookie(response, sessionKey);

                    response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
                    response.setHeader("Location", App.env.getSPAUrl());

                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    e.printStackTrace();
                }
            })) return true;
        }
        if (App.settings.useSamlLogin()) {
            if (match ("/login/okta", path, (a) -> {
                try {
                    String base64 = request.getParameter("SAMLResponse");
                    SAML saml = new SAML(base64);
                    if (!saml.verify()) {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        return;
                    }
                    Name name = saml.name();
                    Mail mail = saml.mail();

                    SessionKey sessionKey = accountService.login(name, mail);
                    setSessionKeyToCookie(response, sessionKey);

                    response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
                    response.setHeader("Location", App.env.getSPAUrl());

                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    e.printStackTrace();
                }
            })) return true;
        }
        return false;
    }

}
