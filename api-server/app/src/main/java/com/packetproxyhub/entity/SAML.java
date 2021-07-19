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
package com.packetproxyhub.entity;

import com.onelogin.saml2.authn.SamlResponse;
import com.onelogin.saml2.exception.SettingsException;
import com.onelogin.saml2.exception.ValidationError;
import com.onelogin.saml2.settings.Saml2Settings;
import com.onelogin.saml2.settings.SettingsBuilder;
import com.packetproxyhub.application.App;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class SAML {

    private Saml2Settings settings;
    private MySamlResponse saml;

    private class MySamlResponse extends SamlResponse {
        public MySamlResponse(Saml2Settings settings, String currentUrl, String samlResponse) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException, SettingsException, ValidationError {
            super(settings, currentUrl, samlResponse);
        }
        @Override
        protected void validateDestination(final Element element) throws ValidationError {
            // Skip Destination Check
        }
    }

    public SAML(String base64SAML) throws Exception {
        Map<String, Object> samlData = new HashMap<>();
        samlData.put("onelogin.saml2.idp.entityid", App.settings.getSamlIdpUrl());
        samlData.put("onelogin.saml2.sp.entityid", App.settings.getSamlSpUrl());
        samlData.put("onelogin.saml2.idp.x509cert", Files.readString(Paths.get(App.settings.getSamlPemPath())));
        SettingsBuilder builder = new SettingsBuilder();
        settings = builder.fromValues(samlData).build();
        saml = new MySamlResponse(settings, App.settings.getSamlSpUrl(), base64SAML);
    }

    public Name name() throws Exception {
        String mail = saml.getNameId();
        String name = StringUtils.substringBefore(mail, "@");
        return Name.create(name);
    }

    public Mail mail() throws Exception {
        String mail = saml.getNameId();
        return Mail.create(mail);
    }

    public boolean verify() {
        return saml.isValid();
    }

}
