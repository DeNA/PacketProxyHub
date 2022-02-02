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

export interface Binary {
    orgId: string
    projectId: string
    configId: string
    id: string
    name: string
    description: string
    fileId: string
    uploadedBy: string
    uploadedAt?: number
}

export const initialBinary = {
    orgId: "",
    projectId: "",
    configId: "",
    id: "",
    name: "",
    description: "",
    fileId: "",
    uploadedBy: "",
    uploadedAt: 0,
}

export const binariesAdapter = createEntityAdapter<Binary>({
    selectId: (binary) => binary.id,
    sortComparer: (a, b) => a.name.localeCompare(b.name),
})

export const binary = createSlice({
    name: "binaries",
    initialState: binariesAdapter.getInitialState(),
    reducers: {
        binariesReset: (state) => binariesAdapter.getInitialState(),
        binaryUpdate: binariesAdapter.updateOne,
        binariesRemove: binariesAdapter.removeMany,
        binariesUpsert: binariesAdapter.upsertMany,
        binariesSync(state, action:PayloadAction<{configId:string,binaries:Binary[]}>) {
            const oldBinaries = binariesAdapter.getSelectors().selectAll(state).filter(p => p.configId === action.payload.configId)
            let newBinaries = action.payload.binaries
            oldBinaries.forEach(old => {
                const found = newBinaries.find(binary => binary.id === old.id)
                if (found) {
                    binariesAdapter.updateOne(state, {id: old.id, changes : found})
                    newBinaries = newBinaries.filter(binary => binary.id !== old.id)
                } else {
                    binariesAdapter.removeOne(state, old.id)
                }
            })
            binariesAdapter.upsertMany(state, newBinaries)
        }
    }
})

export const {
    binariesReset,
    binaryUpdate,
    binariesRemove,
    binariesUpsert,
    binariesSync,
} = binary.actions

export const binariesSelectors = binariesAdapter.getSelectors<RootState>(
    (state) => state.binaries
)

export const selectBinaryByName = (state: RootState, name : string) : Binary[] => {
    const binaries : Binary[] = binariesSelectors.selectAll(state)
    const filtered : Binary[] = binaries.filter((binary) => name === binary.name)
    return filtered
}
