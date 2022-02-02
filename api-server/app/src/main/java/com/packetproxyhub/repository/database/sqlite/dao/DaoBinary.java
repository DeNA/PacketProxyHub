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

import com.packetproxyhub.entity.Binary;
import com.packetproxyhub.entity.Binaries;
import com.packetproxyhub.entity.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class DaoBinary {
    private long dbId;
    private DaoId id;
    private DaoName name;
    private String description;
    private DaoId fileId;
    private DaoId uploadedBy;
    private long uploadedAt;
    private DaoId configId;

    static public Binaries toBinaries(Set<DaoBinary> daoBinaries) {
        Binaries configs = Binaries.create();
        for (DaoBinary daoBinary : daoBinaries) {
            configs.add(daoBinary.toBinary());
        }
        return configs;
    }

    public DaoBinary() {
    }

    public DaoBinary(Id configId, Binary binary) {
        this.dbId = binary.getId().toLong();
        this.id = DaoId.create(binary.getId());
        this.name = DaoName.create(binary.getName());
        this.description = binary.getDescription();
        this.fileId = DaoId.create(binary.getFileId());
        this.uploadedBy = DaoId.create(binary.getUploadedBy());
        this.uploadedAt = binary.getUploadedAt();
        this.configId = DaoId.create(configId);
    }

    public Binary toBinary() {
        return Binary.create(id.toId(), name.toName(), description, fileId.toId(), uploadedBy.toId(), uploadedAt);
    }
}