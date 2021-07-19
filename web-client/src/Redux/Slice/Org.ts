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

export interface Org {
    id: string
    name: string
    description: string
    openState: boolean
}

export const orgsAdapter = createEntityAdapter<Org>({
    selectId: (org) => org.id,
    sortComparer: (a, b) => a.name.localeCompare(b.name),
})

export const org = createSlice({
    name: "orgs",
    initialState: orgsAdapter.getInitialState(),
    reducers: {
        orgsReset: (state) => orgsAdapter.getInitialState(),
        orgsUpdate: orgsAdapter.updateOne,
        orgsRemove: orgsAdapter.removeMany,
        orgsUpsert: orgsAdapter.upsertMany,
        orgsSync(state, action:PayloadAction<Org[]>) {
            const oldOrgs = orgsAdapter.getSelectors().selectAll(state)
            let newOrgs = action.payload
            oldOrgs.forEach(old => {
                const found = newOrgs.find(org => org.id === old.id)
                if (found) {
                    orgsAdapter.updateOne(state, {id: old.id, changes : found})
                    newOrgs = newOrgs.filter(org => org.id !== old.id)
                } else {
                    orgsAdapter.removeOne(state, old.id)
                }
            })
            orgsAdapter.upsertMany(state, newOrgs)
        }
    }
})

export const {
    orgsReset,
    orgsRemove,
    orgsUpdate,
    orgsUpsert,
    orgsSync,
} = org.actions

export const orgsSelectors = orgsAdapter.getSelectors<RootState>(
    (state) => state.orgs
)

export const selectOrgByName = (state: RootState, name : string) : Org | undefined => {
    const orgs : Org[] = orgsSelectors.selectAll(state)
    const filtered : Org[] = orgs.filter((org) => name === org.name)
    return (filtered.length > 0 ? filtered[0] : undefined)
}

