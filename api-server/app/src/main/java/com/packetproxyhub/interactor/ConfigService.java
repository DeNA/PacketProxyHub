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

import com.packetproxyhub.entity.AccessChecker.AccessChecker;
import com.packetproxyhub.entity.Config;
import com.packetproxyhub.entity.Configs;
import com.packetproxyhub.entity.Id;
import com.packetproxyhub.entity.Ids;

import javax.inject.Inject;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ConfigService implements IConfigService {
    @Inject
    private IRepository repository;

    @Override
    public Ids listConfigs(Id accountId, Id orgId, Id projectId) throws Exception {
        if (AccessChecker.create(accountId, repository).isProjectReadable(orgId, projectId) == false) {
            throw new IllegalAccessException();
        }
        return repository.listConfigsInProject(projectId);
    }

    @Override
    public Config getConfig(Id accountId, Id orgId, Id projectId, Id configId) throws Exception {
        if (AccessChecker.create(accountId, repository).isConfigReadable(orgId, projectId, configId) == false) {
            throw new IllegalAccessException();
        }
        return repository.getConfig(configId);
    }

    @Override
    public Configs getConfigs(Id accountId, Id orgId, Id projectId) throws Exception {
        Ids configIds = repository.listConfigsInProject(projectId);
        Set<Config> configSet = configIds.set().stream().map(configId -> repository.getConfig(configId)).collect(Collectors.toSet());
        return Configs.createWithInit(configSet);
    }

    @Override
    public Id createConfig(Id accountId, Id orgId, Id projectId, Config config) throws Exception {
        if (AccessChecker.create(accountId, repository).isProjectReadable(orgId, projectId) == false) {
            throw new IllegalAccessException();
        }
        Config newConfig = config.updatedBy(accountId);
        repository.insertConfigToProject(projectId, newConfig);
        return newConfig.getId();
    }

    @Override
    public void updateConfig(Id accountId, Id orgId, Id projectId, Id configId, Config config) throws Exception {
        if (AccessChecker.create(accountId, repository).isConfigReadable(orgId, projectId, configId) == false) {
            throw new IllegalAccessException();
        }
        if (config.equalsId(configId) == false) {
            throw new UnknownError();
        }
        Config newConfig = config.updatedBy(accountId);
        repository.updateConfig(projectId, newConfig);
    }

    @Override
    public void removeConfig(Id accountId, Id orgId, Id projectId, Id configId) throws Exception {
        if (AccessChecker.create(accountId, repository).isConfigReadable(orgId, projectId, configId) == false) {
            throw new IllegalAccessException();
        }
        repository.removeConfigFromProject(projectId, configId);
    }

}
