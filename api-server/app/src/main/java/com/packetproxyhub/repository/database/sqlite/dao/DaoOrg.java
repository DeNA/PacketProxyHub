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

import com.packetproxyhub.entity.Org;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DaoOrg {
    private Long dbId;
    private DaoId id;
    private DaoName name;
    private String description;

    public DaoOrg() {
    }

    public DaoOrg(Org org) {
        this.dbId = org.getId().toLong();
        this.id = DaoId.create(org.getId());
        this.name = DaoName.create(org.getName());
        this.description = org.getDescription();
    }

    public Org toOrg() {
        return Org.create(id.toId(), name.toName(), description);
    }

}
