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

import {createEntityAdapter, createSlice, PayloadAction} from "@reduxjs/toolkit";
import {RootState} from "../store";

export interface Snapshot {
    orgId: string
    projectId: string
    configId: string
    id: string
    name: string
    description: string
    fileId: string
    screenshotId: string
    uploadedBy: string
    uploadedAt?: number
    resizedScreenshot: string
}

export const initialSnapshot = {
    orgId: "",
    projectId: "",
    configId: "",
    id: "",
    name: "",
    description: "",
    fileId: "",
    screenshotId: "",
    uploadedBy: "",
    uploadedAt: 0,
    resizedScreenshot: '',
}

export const snapshotsAdapter = createEntityAdapter<Snapshot>({
    selectId: (snapshot) => snapshot.id,
    sortComparer: (a, b) => a.name.localeCompare(b.name),
})

export const snapshot = createSlice({
    name: "snapshots",
    initialState: snapshotsAdapter.getInitialState(),
    reducers: {
        snapshotsReset: (state) => snapshotsAdapter.getInitialState(),
        snapshotUpdate: snapshotsAdapter.updateOne,
        snapshotsRemove: snapshotsAdapter.removeMany,
        snapshotsUpsert: snapshotsAdapter.upsertMany,
        snapshotsSync(state, action:PayloadAction<{configId:string,snapshots:Snapshot[]}>) {
            const oldSnapshots = snapshotsAdapter.getSelectors().selectAll(state).filter(p => p.configId === action.payload.configId)
            let newSnapshots = action.payload.snapshots
            oldSnapshots.forEach(old => {
                const found = newSnapshots.find(snapshot => snapshot.id === old.id)
                if (found) {
                    snapshotsAdapter.updateOne(state, {id: old.id, changes : found})
                    newSnapshots = newSnapshots.filter(snapshot => snapshot.id !== old.id)
                } else {
                    snapshotsAdapter.removeOne(state, old.id)
                }
            })
            snapshotsAdapter.upsertMany(state, newSnapshots)
        }
    }
})

export const {
    snapshotsReset,
    snapshotUpdate,
    snapshotsRemove,
    snapshotsUpsert,
    snapshotsSync,
} = snapshot.actions

export const snapshotsSelectors = snapshotsAdapter.getSelectors<RootState>(
    (state) => state.snapshots
)

export const selectSnapshotByName = (state: RootState, name : string) : Snapshot[] => {
    const snapshots : Snapshot[] = snapshotsSelectors.selectAll(state)
    const filtered : Snapshot[] = snapshots.filter((snapshot) => name === snapshot.name)
    return filtered
}
