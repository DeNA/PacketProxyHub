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

import {createEntityAdapter, createSlice} from "@reduxjs/toolkit";
import {RootState} from "../store";

export interface TreeOpenState {
    id: string
    openState: boolean
}

export const initialTreeOpenState = {
    id: "",
    openState: false,
}

export const treeOpenStatesAdapter = createEntityAdapter<TreeOpenState>({
    selectId: (treeOpenState) => treeOpenState.id,
    sortComparer: (a, b) => a.id.localeCompare(b.id),
})

export const treeOpenState = createSlice({
    name: "treeOpenStates",
    initialState: treeOpenStatesAdapter.getInitialState(),
    reducers: {
        treeOpenStatesReset: (state) => treeOpenStatesAdapter.getInitialState(),
        treeOpenStatesDelete: treeOpenStatesAdapter.removeMany,
        treeOpenStatesUpsert: treeOpenStatesAdapter.upsertMany,
        treeOpenStateUpdate: treeOpenStatesAdapter.updateOne,
        treeOpenStatesAdd: treeOpenStatesAdapter.addMany,
    }
})

export const {
    treeOpenStatesDelete,
    treeOpenStatesReset,
    treeOpenStatesUpsert,
    treeOpenStateUpdate,
    treeOpenStatesAdd,
} = treeOpenState.actions

export const treeOpenStatesSelectors = treeOpenStatesAdapter.getSelectors<RootState>(
    (state) => state.treeOpenStates
)
