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
package com.packetproxyhub.application;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.util.HttpCookieStore;

import java.util.Map;

public class ApiClient {

    private HttpClient client;
    private HttpCookieStore cookieStore = new HttpCookieStore();
    private String baseUri = "http://localhost:1234";

    public ApiClient() throws Exception {
        client = new HttpClient();
        client.setCookieStore(cookieStore);
        client.start();
    }

    public String get(String path) throws Exception {
        Request request = client.newRequest(baseUri + path);
        request.method(HttpMethod.GET);
        return sendRequest(request, null, null);
    }

    public String post(String path, Map<String,String> params, String body) throws Exception {
        Request request = client.newRequest(baseUri + path);
        request.method(HttpMethod.POST);
        return sendRequest(request, params, body);
    }

    public String put(String path, String body) throws Exception {
        Request request = client.newRequest(baseUri + path);
        request.method(HttpMethod.PUT);
        return sendRequest(request, null, body);
    }

    public String delete(String path) throws Exception {
        Request request = client.newRequest(baseUri + path);
        request.method(HttpMethod.DELETE);
        return sendRequest(request, null, null);
    }

    public String sendRequest(Request request, Map<String,String> params, String body) throws Exception {
        if (body != null) {
            request.content(new StringContentProvider(body));
            request.header("Content-Type", "application/json");
        }
        if (params != null) {
            for (var param : params.entrySet()) {
                request.param(param.getKey(), param.getValue());
            }
        }
        ContentResponse res = request.send();
        if (res.getStatus() != HttpStatus.OK_200) {
            throw new Exception(
                    String.format(
                            "[HTTP Error] %d %s : %s",
                            res.getStatus(),
                            HttpStatus.getMessage(res.getStatus()),
                            res.getContentAsString()
                    )
            );
        }
        return res.getContentAsString();
    }

}
