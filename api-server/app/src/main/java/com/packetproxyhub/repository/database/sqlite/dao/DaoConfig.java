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
package com.packetproxyhub.repository.database.sqlite.dao;

import com.packetproxyhub.entity.Config;
import com.packetproxyhub.entity.Configs;
import com.packetproxyhub.entity.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class DaoConfig {
    private long dbId;
    private DaoId id;
    private DaoName name;
    private String description;
    private String packetProxyConf;
    private String pfConf;
    private String memo;
    private DaoId accountId;
    private long updatedAt;
    private DaoId projectId;

    static public Configs toConfigs(Set<DaoConfig> daoConfigs) {
        Configs configs = Configs.create();
        for (DaoConfig daoConfig : daoConfigs) {
            configs.add(daoConfig.toConfig());
        }
        return configs;
    }

    public DaoConfig() {
    }

    public DaoConfig(Id projectId, Config config) {
        this.dbId = config.getId().toLong();
        this.id = DaoId.create(config.getId());
        this.name = DaoName.create(config.getName());
        this.description = config.getDescription();
        this.packetProxyConf = config.getPacketProxyConf();
        this.pfConf = config.getPfConf();
        this.memo = config.getMemo();
        this.accountId = DaoId.create(config.getAccountId());
        this.updatedAt = config.getUpdatedAt();
        this.projectId = DaoId.create(projectId);
    }

    public Config toConfig() {
        return Config.create(id.toId(), name.toName(), description, packetProxyConf, pfConf, memo, accountId.toId(), updatedAt);
    }

}
