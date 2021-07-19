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

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.packetproxyhub.controller.WebApiController;
import com.packetproxyhub.interactor.*;
import com.packetproxyhub.repository.sqlite.SqliteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {

    static private final String APP_SETTINGS_FILE = "/etc/packetproxyhub/packetproxyhub.properties";
    static public Logger logger;
    static public AppSettings settings;
    static public AppEnv env;

    static public void main(String[] args) {

        logger = LoggerFactory.getLogger(App.class);
        logger.info("PacketProxyHub API Server");

        settings = AppSettings.createFromFile(APP_SETTINGS_FILE);
        logger.info("read setting file from " + APP_SETTINGS_FILE);
        env = new AppEnv();

        // DI
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override protected void configure() {
                bind(IAccountService.class).to(AccountService.class).asEagerSingleton();
                bind(IProjectService.class).to(ProjectService.class).asEagerSingleton();
                bind(IConfigService.class).to(ConfigService.class).asEagerSingleton();
                bind(IOrgService.class).to(OrgService.class).asEagerSingleton();
                bind(IOrgMemberService.class).to(OrgMemberService.class).asEagerSingleton();
                bind(IResourceService.class).to(ResourceService.class).asEagerSingleton();
                bind(IRepository.class).to(SqliteRepository.class).asEagerSingleton();
            }
        });

        try {
            WebApiController webApiController = injector.getInstance(WebApiController.class);
            webApiController.run(injector);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    static public void setupTest() {
        settings = AppSettings.createFromResource("/test.properties");
        env = new AppEnv();
    }

}
