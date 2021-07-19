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

import org.junit.jupiter.api.Test;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class AppSettingsTest {

    @Test
    public void 正常にキーが読み込めること() throws Exception {
        AppSettings settings = AppSettings.createFromString("API_SERVER_URL=https://a.b.c.d:1234/");
        String url = settings.getApiServerUrlRoot();
        assertNotNull(url);
    }

    @Test
    public void URLクラスのテスト() throws Exception {
        URL url = new URL("https://user:password@a.b.c.d:1234/path/to/file?aaa=bbb");
        System.out.println(url);
        System.out.println("protocol:"+url.getProtocol());
        System.out.println("authority:"+url.getAuthority());
        System.out.println("host:"+url.getHost());
        System.out.println("port:"+url.getPort());
        System.out.println("path:"+url.getPath());
        System.out.println("query:"+url.getQuery());
        System.out.println("userinfo:"+url.getUserInfo());
    }

    @Test
    public void URLクラスのパス無しURLのテスト() throws Exception {
        URL url = new URL("https://a.b.c.d:1234");
        System.out.println(url);
        System.out.println("protocol:"+url.getProtocol());
        System.out.println("authority:"+url.getAuthority());
        System.out.println("host:"+url.getHost());
        System.out.println("port:"+url.getPort());
        System.out.println("path:"+url.getPath());
        System.out.println("query:"+url.getQuery());
        System.out.println("userinfo:"+url.getUserInfo());
    }

    @Test
    public void URLクラスのHTTPSのデフォルトポートのテスト() throws Exception {
        URL url = new URL("https://a.b.c.d/");
        System.out.println(url);
        System.out.println("port:"+url.getPort());
        System.out.println("default port:"+url.getDefaultPort());
    }

    @Test
    public void URLクラスのHTTPのデフォルトポートのテスト() throws Exception {
        URL url = new URL("http://a.b.c.d/");
        System.out.println(url);
        System.out.println("port:"+url.getPort());
        System.out.println("default port:"+url.getDefaultPort());
    }

}