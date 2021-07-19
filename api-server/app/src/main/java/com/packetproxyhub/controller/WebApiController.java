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

import com.google.inject.Injector;
import com.packetproxyhub.application.App;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

public class WebApiController {

    public void run(Injector injector) throws Exception {
        Server server = null;
        if (App.env.isProductionEnv()) {
            if (App.settings.useSSL()) {
                server = new Server();
                server.addConnector(createHttpsConnector(server, App.settings.getApiServerPort()));
            } else {
                server = new Server(App.settings.getApiServerPort());
            }
        }
        if (App.env.isDevelopmentEnv()) {
            server = new Server(1234);
        }
        if (server == null) {
            throw new Exception("Unknown App.env");
        }

        server.setHandler(createHandlers(injector));
        server.start();
        server.join();
    }

    public ServerConnector createHttpsConnector(Server server, int httpsPort) {
        SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setKeyStorePath(App.settings.getApiServerCertPath());
        sslContextFactory.setKeyStorePassword(App.settings.getApiServerCertPassword());
        sslContextFactory.setKeyManagerPassword(App.settings.getApiServerCertPassword());
        sslContextFactory.setMaxCertPathLength(10);

        HttpConfiguration httpsConf = new HttpConfiguration();
        httpsConf.setSecureScheme("https");
        httpsConf.setSecurePort(httpsPort);
        httpsConf.addCustomizer(new SecureRequestCustomizer()); // adds ssl info to request object

        ServerConnector httpsConnector = new ServerConnector(server,
                new SslConnectionFactory(sslContextFactory, "http/1.1"),
                new HttpConnectionFactory(httpsConf));
        httpsConnector.setPort(httpsPort);

        return httpsConnector;
    }

    private HandlerList createHandlers(Injector injector) {
        HandlerList handlers = new HandlerList();
        handlers.addHandler(createServletContextHandler(injector));
        handlers.addHandler(new DefaultHandler()); // for non-context error handling
        return handlers;
    }

    private FilterHolder createCORSFilter() {
        FilterHolder holder = new FilterHolder(new CrossOriginFilter());
        if (App.env.isProductionEnv()) {
            holder.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, App.settings.getWebServerUrlWithoutPath());
        }
        if (App.env.isDevelopmentEnv()) {
            holder.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "http://localhost:3000");
        }
        holder.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM,  "Authorization,Content-Type");
        holder.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM,  "GET,HEAD,OPTIONS,PUT,PATCH,POST,DELETE");
        holder.setInitParameter(CrossOriginFilter.ALLOW_CREDENTIALS_PARAM,  "true");
        return holder;
    }

    private ServletContextHandler createServletContextHandler(Injector injector) {
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        context.addEventListener(new ServletFactory(injector));
        context.addServlet(DefaultServlet.class, "/"); // for context based static file serving
        context.addFilter(createCORSFilter(), "/*", EnumSet.allOf(DispatcherType.class));
        return context;
    }

}