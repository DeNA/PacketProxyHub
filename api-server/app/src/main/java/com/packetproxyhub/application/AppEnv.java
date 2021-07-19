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

public class AppEnv {
    public enum APP_ENV { DEVELOPMENT, PRODUCTION };

    private APP_ENV env = APP_ENV.DEVELOPMENT;

    public boolean isDevelopmentEnv() {
        return env == APP_ENV.DEVELOPMENT;
    }

    public boolean isProductionEnv() {
        return env == APP_ENV.PRODUCTION;
    }

    public String getSPAUrl() {
        if (isDevelopmentEnv()) {
            return "http://localhost:3000/";
        }
        if (isProductionEnv()) {
            return App.settings.getWebServerUrlRoot();
        }
        return "";
    }

    public AppEnv() {
        String appEnv = System.getenv("APP_ENV");
        if (appEnv != null && appEnv.equalsIgnoreCase("production")) {
            env = APP_ENV.PRODUCTION;
        }
    }

}
