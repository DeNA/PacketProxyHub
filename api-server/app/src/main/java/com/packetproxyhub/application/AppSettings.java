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

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class AppSettings {

    static public AppSettings createFromFile(String filePath) {
        Properties props = new Properties();
        try (FileInputStream inputStream = new FileInputStream(filePath)) {
            props.load(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            App.logger.error(String.format("[Error] cannot open config file (%s)", filePath));
            System.exit(1);
        }
        return new AppSettings(props);
    }

    static public AppSettings createFromString(String settings) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(settings.getBytes(StandardCharsets.UTF_8));
        Properties props = new Properties();
        props.load(bais);
        return new AppSettings(props);
    }

    static public AppSettings createFromResource(String resourcePath) {
        Properties props = new Properties();
        try (InputStream is = App.class.getResourceAsStream(resourcePath)) {
             props.load(is);
        } catch (Exception e) {
            e.printStackTrace();
            App.logger.error(String.format("[Error] cannot open resource file (%s)", resourcePath));
            System.exit(1);
        }
        return new AppSettings(props);
    }

    private Properties properties;

    private AppSettings(Properties props) {
        this.properties = props;
    }

    private URL getURL(String urlString) {
        try {
            return new URL(urlString);
        } catch (Exception e) {
            e.printStackTrace();
            App.logger.error(e.toString());
            System.exit(1);
        }
        return null;
    }

    private URL getWebServerUrl() {
        String urlString = properties.getProperty("WEB_SERVER_URL");
        return getURL(urlString);
    }

    public String getWebServerUrlRoot() {
        URL url = getWebServerUrl();
        return String.format("%s://%s/", url.getProtocol(), url.getAuthority());
    }

    public String getWebServerUrlWithoutPath() {
        URL url = getWebServerUrl();
        return String.format("%s://%s", url.getProtocol(), url.getAuthority());
    }

    private URL getApiServerUrl() {
        String urlString = properties.getProperty("API_SERVER_URL");
        return getURL(urlString);
    }

    public String getApiServerUrlRoot() {
        URL url = getApiServerUrl();
        return String.format("%s://%s/", url.getProtocol(), url.getAuthority());
    }

    public String getApiServerUrlWithoutPath() {
        URL url = getApiServerUrl();
        return String.format("%s://%s", url.getProtocol(), url.getAuthority());
    }

    public int getApiServerPort() {
        URL url = getApiServerUrl();
        int port = url.getPort();
        return port < 0 ? url.getDefaultPort() : port;
    }

    public boolean useSSL() {
        URL url = getApiServerUrl();
        return url.getProtocol().equalsIgnoreCase("https");
    }

    public String getApiServerCertPath() {
        return properties.getProperty("API_SERVER_SSL_CERT_PATH");
    }

    public String getApiServerCertPassword() {
        return properties.getProperty("API_SERVER_SSL_CERT_PASSWORD");
    }

    public boolean useGoogleLogin() {
        String value = properties.getProperty("GOOGLE_LOGIN_ENABLED");
        return value.equalsIgnoreCase("true");
    }

    public String getGoogleClientId() {
        return properties.getProperty("GOOGLE_CLIENT_ID", "GoogleTestClientId");
    }

    public boolean useSamlLogin() {
        String value = properties.getProperty("SAML_LOGIN_ENABLED");
        return value.equalsIgnoreCase("true");
    }

    public String getSamlIdpUrl() {
        return properties.getProperty("SAML_IDP_URL");
    }

    public String getSamlSpUrl() {
        return String.format("%s/login/okta", getApiServerUrlWithoutPath());
    }

    public String getSamlPemPath() {
        return properties.getProperty("SAML_PEM_PATH");
    }

    public String getSessionCryptKey() {
        return properties.getProperty("SESSION_CRYPT_KEY");
    }

    public String getDatabasePath() {
        return properties.getProperty("DATABASE_PATH");
    }

    public String getDataDirPath() {
        return properties.getProperty("DATA_DIR_PATH");
    }

}
