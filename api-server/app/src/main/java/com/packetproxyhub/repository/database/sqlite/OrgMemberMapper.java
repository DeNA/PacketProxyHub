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
package com.packetproxyhub.repository.database.sqlite;

import com.packetproxyhub.repository.database.sqlite.dao.DaoId;
import com.packetproxyhub.repository.database.sqlite.dao.DaoOrgMember;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

public interface OrgMemberMapper {
    void createTable();
    Set<DaoOrgMember> selectAll();
    Set<DaoOrgMember> selectByAccountId(DaoId accountId);
    Set<DaoOrgMember> selectByOrgId(DaoId orgId);
    Set<DaoOrgMember> selectByIds(@Param("ids") Set<DaoId> ids);
    DaoOrgMember select(DaoId id);
    void insert(DaoOrgMember orgMember);
    void update(DaoOrgMember orgMember);
    void delete(DaoId id);
}
