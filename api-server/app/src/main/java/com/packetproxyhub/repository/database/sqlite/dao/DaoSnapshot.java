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

import com.packetproxyhub.entity.Snapshot;
import com.packetproxyhub.entity.Snapshots;
import com.packetproxyhub.entity.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class DaoSnapshot {
    private long dbId;
    private DaoId id;
    private DaoName name;
    private String description;
    private String androidVersion;
    private long googlePlay;
    private DaoId fileId;
    private DaoId screenshotId;
    private DaoId uploadedBy;
    private long uploadedAt;
    private DaoId configId;

    static public Snapshots toSnapshots(Set<DaoSnapshot> daoSnapshots) {
        Snapshots configs = Snapshots.create();
        for (DaoSnapshot daoSnapshot : daoSnapshots) {
            configs.add(daoSnapshot.toSnapshot());
        }
        return configs;
    }

    public DaoSnapshot() {
    }

    public DaoSnapshot(Id configId, Snapshot snapshot) {
        this.dbId = snapshot.getId().toLong();
        this.id = DaoId.create(snapshot.getId());
        this.name = DaoName.create(snapshot.getName());
        this.description = snapshot.getDescription();
        this.androidVersion = snapshot.getAndroidVersion();
        this.googlePlay = snapshot.getGooglePlay();
        this.fileId = DaoId.create(snapshot.getFileId());
        this.screenshotId = DaoId.create(snapshot.getScreenshotId());
        this.uploadedBy = DaoId.create(snapshot.getUploadedBy());
        this.uploadedAt = snapshot.getUploadedAt();
        this.configId = DaoId.create(configId);
    }

    public Snapshot toSnapshot() {
        return Snapshot.create(id.toId(), name.toName(), description, androidVersion, googlePlay, fileId.toId(), screenshotId.toId(), uploadedBy.toId(), uploadedAt);
    }
}