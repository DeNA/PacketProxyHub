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
import com.packetproxyhub.entity.Snapshot;
import com.packetproxyhub.entity.Snapshots;
import com.packetproxyhub.entity.Id;
import com.packetproxyhub.entity.Ids;

import javax.inject.Inject;
import java.util.Set;
import java.util.stream.Collectors;

public class SnapshotService implements ISnapshotService {
    @Inject
    private IRepository repository;

    @Override
    public Ids listSnapshots(Id accountId, Id orgId, Id projectId, Id configId) throws Exception {
        if (AccessChecker.create(accountId, repository).isConfigReadable(orgId, projectId, configId) == false) {
            throw new IllegalAccessException();
        }
        return repository.listSnapshotsInConfig(configId);
    }

    @Override
    public Snapshot getSnapshot(Id accountId, Id orgId, Id projectId, Id configId, Id snapshotId) throws Exception {
        if (AccessChecker.create(accountId, repository).isConfigReadable(orgId, projectId, configId) == false) {
            throw new IllegalAccessException();
        }
        return repository.getSnapshot(snapshotId);
    }

    @Override
    public Snapshots getSnapshots(Id accountId, Id orgId, Id projectId, Id configId) throws Exception {
        Ids snapshotIds = repository.listSnapshotsInConfig(configId);
        Set<Snapshot> snapshotSet = snapshotIds.set().stream().map(snapshotId -> repository.getSnapshot(snapshotId)).collect(Collectors.toSet());
        return Snapshots.createWithInit(snapshotSet);
    }

    @Override
    public Id createSnapshot(Id accountId, Id orgId, Id projectId, Id configId, Snapshot snapshot) throws Exception {
        if (AccessChecker.create(accountId, repository).isConfigWritable(orgId, projectId, configId) == false) {
            throw new IllegalAccessException();
        }
        Snapshot newSnapshot = snapshot.uploadedBy(accountId);
        repository.insertSnapshotToConfig(configId, newSnapshot);
        return newSnapshot.getId();
    }

    @Override
    public void updateSnapshot(Id accountId, Id orgId, Id projectId, Id configId, Id snapshotId, Snapshot snapshot) throws Exception {
        if (AccessChecker.create(accountId, repository).isConfigReadable(orgId, projectId, configId) == false) {
            throw new IllegalAccessException();
        }
        if (snapshot.equalsId(snapshotId) == false) {
            throw new UnknownError();
        }
        repository.updateSnapshot(projectId, snapshot);
    }

    @Override
    public void removeSnapshot(Id accountId, Id orgId, Id projectId, Id configId, Id snapshotId) throws Exception {
        if (AccessChecker.create(accountId, repository).isConfigReadable(orgId, projectId, configId) == false) {
            throw new IllegalAccessException();
        }
        repository.removeSnapshotFromConfig(configId, snapshotId);
    }
}