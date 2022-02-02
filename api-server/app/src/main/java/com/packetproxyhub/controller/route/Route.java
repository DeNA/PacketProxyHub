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
import com.packetproxyhub.entity.Account;
import com.packetproxyhub.entity.Id;
import com.packetproxyhub.entity.SessionKey;
import com.packetproxyhub.interactor.IAccountService;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.uri.UriTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

abstract class Route {
    @Inject
    private IAccountService accountService;

    boolean getIfMatch(String path, HttpServletRequest request, HttpServletResponse response) {
        return false;
    }

    boolean postIfMatch(String path, HttpServletRequest request, HttpServletResponse response) {
        return false;
    }

    boolean putIfMatch(String path, HttpServletRequest request, HttpServletResponse response) {
        return false;
    }

    boolean deleteIfMatch(String path, HttpServletRequest request, HttpServletResponse response) {
        return false;
    }

    protected boolean match(String template, String path, Consumer<Map<String,String>> func) {
        UriTemplate uriTemplate = new UriTemplate(template);
        Map<String, String> parameters = new HashMap<>();
        if (uriTemplate.match(path, parameters)) {
            func.accept(parameters);
            return true;
        }
        return false;
    }

    protected Id getAccountId(HttpServletRequest request) throws Exception {
        return getAccount(request).getId();
    }

    protected Account getAccount(HttpServletRequest request) throws Exception {
        SessionKey sessionKey = getSessionKey(request);
        return accountService.findAccountBy(sessionKey);
    }

    protected SessionKey getSessionKey(HttpServletRequest request) throws Exception {
        Cookie[] cookies = request.getCookies();
        Optional<Cookie> optCookie = Arrays.stream(cookies).filter(c -> c.getName().equals("packetproxyhub_session")).findFirst();
        if (optCookie.isEmpty()) {
            throw new IllegalAccessException();
        }
        return SessionKey.createFromSessionKeyString(optCookie.get().getValue());
    }

    protected void replyResponseAsJson(HttpServletResponse response, String json) throws Exception {
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().println(json);
    }

    protected void replyResponseAsBinary(HttpServletResponse response, InputStream in) throws Exception {
        response.setContentType("application/octet-stream");
        OutputStream out = response.getOutputStream();
        IOUtils.copy(in, out);
    }
}