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
import com.packetproxyhub.entity.Binary;
import com.packetproxyhub.entity.Binaries;
import com.packetproxyhub.entity.Id;
import com.packetproxyhub.entity.Ids;

import javax.inject.Inject;
import java.util.Set;
import java.util.stream.Collectors;

public class BinaryService implements IBinaryService {
    @Inject
    private IRepository repository;

    @Override
    public Ids listBinaries(Id accountId, Id orgId, Id projectId, Id configId) throws Exception {
        if (AccessChecker.create(accountId, repository).isConfigReadable(orgId, projectId, configId) == false) {
            throw new IllegalAccessException();
        }
        return repository.listBinariesInConfig(configId);
    }

    @Override
    public Binary getBinary(Id accountId, Id orgId, Id projectId, Id configId, Id binaryId) throws Exception {
        if (AccessChecker.create(accountId, repository).isConfigReadable(orgId, projectId, configId) == false) {
            throw new IllegalAccessException();
        }
        return repository.getBinary(binaryId);
    }

    @Override
    public Binaries getBinaries(Id accountId, Id orgId, Id projectId, Id configId) throws Exception {
        Ids binaryIds = repository.listBinariesInConfig(configId);
        Set<Binary> binarySet = binaryIds.set().stream().map(binaryId -> repository.getBinary(binaryId)).collect(Collectors.toSet());
        return Binaries.createWithInit(binarySet);
    }

    @Override
    public Id createBinary(Id accountId, Id orgId, Id projectId, Id configId, Binary binary) throws Exception {
        if (AccessChecker.create(accountId, repository).isConfigWritable(orgId, projectId, configId) == false) {
            throw new IllegalAccessException();
        }
        Binary newBinary = binary.uploadedBy(accountId);
        repository.insertBinaryToConfig(configId, newBinary);
        return newBinary.getId();
    }

    @Override
    public void updateBinary(Id accountId, Id orgId, Id projectId, Id configId, Id binaryId, Binary binary) throws Exception {
        if (AccessChecker.create(accountId, repository).isConfigReadable(orgId, projectId, configId) == false) {
            throw new IllegalAccessException();
        }
        if (binary.equalsId(binaryId) == false) {
            throw new UnknownError();
        }
        repository.updateBinary(projectId, binary);
    }

    @Override
    public void removeBinary(Id accountId, Id orgId, Id projectId, Id configId, Id binaryId) throws Exception {
        if (AccessChecker.create(accountId, repository).isConfigReadable(orgId, projectId, configId) == false) {
            throw new IllegalAccessException();
        }
        repository.removeBinaryFromConfig(configId, binaryId);
    }

}
