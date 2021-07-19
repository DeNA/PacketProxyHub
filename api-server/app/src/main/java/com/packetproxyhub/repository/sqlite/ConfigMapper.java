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
package com.packetproxyhub.repository.sqlite;

import com.packetproxyhub.repository.sqlite.dao.DaoConfig;
import com.packetproxyhub.repository.sqlite.dao.DaoId;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

public interface ConfigMapper {
    void createTable();
    DaoConfig select(DaoId configId);
    Set<DaoConfig> selectAll();
    Set<DaoConfig> selectByProjectId(DaoId projectId);
    void insert(DaoConfig daoConfig);
    void update(DaoConfig daoConfig);
    void bulkInsert(@Param("daoConfigs") Set<DaoConfig> daoConfigs);
    void bulkInsertIfNotExists(@Param("daoConfigs") Set<DaoConfig> daoConfigs);
    void delete(DaoId configId);
}
