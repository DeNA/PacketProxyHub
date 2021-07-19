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
package com.packetproxyhub.interactor;

import com.packetproxyhub.entity.*;

public interface IConfigService {
    Ids listConfigs(Id accountId, Id orgId, Id projectId) throws Exception;
    Config getConfig(Id accountId, Id orgId, Id projectId, Id configId) throws Exception;
    Configs getConfigs(Id accountId, Id orgId, Id projectId) throws Exception;
    Id createConfig(Id accountId, Id orgId, Id projectId, Config config) throws Exception;
    void updateConfig(Id accountId, Id orgId, Id projectId, Id configId, Config config) throws Exception;
    void removeConfig(Id accountId, Id orgId, Id projectId, Id configId) throws Exception;
}
