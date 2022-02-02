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

import com.packetproxyhub.entity.Name;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DaoName {

    static public DaoName create(Name name) {
        return new DaoName(name);
    }

    private String name;

    public DaoName() {
    }

    public DaoName(Name name) {
        this.name = name.toString();
    }

    public Name toName() {
        return Name.create(name);
    }
}
