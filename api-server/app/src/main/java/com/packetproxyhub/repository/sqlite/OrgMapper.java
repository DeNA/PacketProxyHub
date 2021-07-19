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

import com.packetproxyhub.repository.sqlite.dao.DaoId;
import com.packetproxyhub.repository.sqlite.dao.DaoName;
import com.packetproxyhub.repository.sqlite.dao.DaoOrg;

public interface OrgMapper {
    void createTable();
    DaoOrg selectById(DaoId name);
    DaoOrg selectByName(DaoName name);
    void insert(DaoOrg org);
    void update(DaoOrg org);
    void delete(DaoId name);
}
