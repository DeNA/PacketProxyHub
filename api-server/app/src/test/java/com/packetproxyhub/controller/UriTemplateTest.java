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
package com.packetproxyhub.controller;

import org.glassfish.jersey.uri.UriTemplate;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UriTemplateTest {
    @Test
    public void uriTemplateが正常に動作する() {
        String template = "/projects/{projectId}/configs/{configId}";
        String uri      = "/projects/11-22-33-44/configs/44-33-22-11";

        UriTemplate uriTemplate = new UriTemplate(template);
        Map<String, String> parameters = new HashMap<>();
        assertTrue(uriTemplate.match(uri, parameters));
    }

    @Test
    public void 最後にスラッシュが存在するとき正常に動作する() {
        String template = "/projects/{projectId}/configs/{configId}";
        String uri      = "/projects/11-22-33-44/configs/";

        UriTemplate uriTemplate = new UriTemplate(template);
        Map<String, String> parameters = new HashMap<>();
        assertFalse(uriTemplate.match(uri, parameters));
    }

    @Test
    public void 最後にスラッシュがない時エラーになる() {
        String template = "/projects/{projectId}/configs/{configId}";
        String uri      = "/projects/11-22-33-44/configs";

        UriTemplate uriTemplate = new UriTemplate(template);
        Map<String, String> parameters = new HashMap<>();
        assertFalse(uriTemplate.match(uri, parameters));
    }

    @Test
    public void 似ているURIのとき() {
        String template = "/projects/{projectId}/configs/";
        String uri      = "/projects/11-22-33-44/configs";

        UriTemplate uriTemplate = new UriTemplate(template);
        Map<String, String> parameters = new HashMap<>();
        assertFalse(uriTemplate.match(uri, parameters));
    }

    @Test
    public void 似ているURIのとき2() {
        String template = "/projects/{projectId}/configs";
        String uri      = "/projects/11-22-33-44/configs/";

        UriTemplate uriTemplate = new UriTemplate(template);
        Map<String, String> parameters = new HashMap<>();
        assertFalse(uriTemplate.match(uri, parameters));
    }

    @Test
    public void URLが長い時はマッチしない() {
        String template = "/projects/{projectId}/configs/{configId}";
        String uri      = "/projects/11-22-33-44/configs/44-33-22-11/hoge";

        UriTemplate uriTemplate = new UriTemplate(template);
        Map<String, String> parameters = new HashMap<>();
        assertFalse(uriTemplate.match(uri, parameters));
    }

}
