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

import com.packetproxyhub.entity.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DaoId {

    static public DaoId create(Id id) {
        return new DaoId(id);
    }

    private String id;

    public DaoId() {
    }

    public DaoId(Id id) {
        this.id = id.toString();
    }

    public Id toId() {
        return Id.create(UUID.fromString(id));
    }
}
