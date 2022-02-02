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

export interface Config {
    orgId: string
    projectId: string
    id: string
    name: string
    description: string
    packetProxyConf: string
    pfConf: string
    fridaScript: string
    memo: string
    accountId: string
    updatedAt?: number
}

export const initialConfig = {
    orgId: "",
    projectId: "",
    id: "",
    name: "",
    description: "",
    packetProxyConf: "",
    pfConf: "",
    fridaScript: "",
    memo: "",
    accountId: "",
    updatedAt: 0,
}

export const configsAdapter = createEntityAdapter<Config>({
    selectId: (config) => config.id,
    sortComparer: (a, b) => a.name.localeCompare(b.name),
})

export const config = createSlice({
    name: "configs",
    initialState: configsAdapter.getInitialState(),
    reducers: {
        configsReset: (state) => configsAdapter.getInitialState(),
        configUpdate: configsAdapter.updateOne,
        configsRemove: configsAdapter.removeMany,
        configsUpsert: configsAdapter.upsertMany,
        configsSync(state, action:PayloadAction<{projectId:string,configs:Config[]}>) {
            const oldConfigs = configsAdapter.getSelectors().selectAll(state).filter(p => p.projectId === action.payload.projectId)
            let newConfigs = action.payload.configs
            oldConfigs.forEach(old => {
                const found = newConfigs.find(config => config.id === old.id)
                if (found) {
                    configsAdapter.updateOne(state, {id: old.id, changes : found})
                    newConfigs = newConfigs.filter(config => config.id !== old.id)
                } else {
                    configsAdapter.removeOne(state, old.id)
                }
            })
            configsAdapter.upsertMany(state, newConfigs)
        }
    }
})

export const {
    configsReset,
    configUpdate,
    configsRemove,
    configsUpsert,
    configsSync,
} = config.actions

export const configsSelectors = configsAdapter.getSelectors<RootState>(
    (state) => state.configs
)

export const selectConfigByName = (state: RootState, name : string) : Config[] => {
    const configs : Config[] = configsSelectors.selectAll(state)
    const filtered : Config[] = configs.filter((config) => name === config.name)
    return filtered
}
