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

import com.packetproxyhub.repository.sqlite.dao.DaoAccount;
import com.packetproxyhub.repository.sqlite.dao.DaoId;

import java.util.Set;

public interface AccountMapper {
    void createTable();
    Set<DaoAccount> selectAll();
    DaoAccount selectById(DaoId id);
    DaoAccount selectByMail(String mail);
    Set<DaoAccount> searchByName(String nameKey);
    void insert(DaoAccount account);
    void update(DaoAccount account);
    void delete(DaoId id);
}
