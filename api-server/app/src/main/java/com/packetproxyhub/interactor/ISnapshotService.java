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

import com.packetproxyhub.entity.Id;
import com.packetproxyhub.entity.Ids;
import com.packetproxyhub.entity.Snapshot;
import com.packetproxyhub.entity.Snapshots;

public interface ISnapshotService {
    Ids listSnapshots(Id accountId, Id orgId, Id projectId, Id configId) throws Exception;
    Snapshots getSnapshots(Id accountId, Id orgId, Id projectId, Id configId) throws Exception;
    Id createSnapshot(Id accountId, Id orgId, Id projectId, Id configId, Snapshot snapshot) throws Exception;
    Snapshot getSnapshot(Id accountId, Id orgId, Id projectId, Id configId, Id snapshotId) throws Exception;
    void updateSnapshot(Id accountId, Id orgId, Id projectId, Id configId, Id snapshotId, Snapshot snapshot) throws Exception;
    void removeSnapshot(Id accountId, Id orgId, Id projectId, Id configId, Id snapshotId) throws Exception;
}
