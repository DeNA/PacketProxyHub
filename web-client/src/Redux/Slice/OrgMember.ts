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

export interface OrgMember {
    orgId: string
    id: string
    accountId: string
    role: string
}

export const initialOrgMember = {
    orgId: "",
    id: "",
    accountId: "",
    role: "Owner",
}

export const orgMembersAdapter = createEntityAdapter<OrgMember>({
    selectId: (orgMember) => orgMember.id,
    sortComparer: (a, b) => a.id.localeCompare(b.id),
})

export const orgMember = createSlice({
    name: "orgMembers",
    initialState: orgMembersAdapter.getInitialState(),
    reducers: {
        orgMembersReset: (state) => orgMembersAdapter.getInitialState(),
        orgMembersDelete: orgMembersAdapter.removeMany,
        orgMembersUpsert:  orgMembersAdapter.upsertMany,
        orgMemberUpdate: orgMembersAdapter.updateOne,
        orgMembersSync(state, action:PayloadAction<{orgId:string,orgMembers:OrgMember[]}>) {
            const oldOrgMembers = orgMembersAdapter.getSelectors().selectAll(state).filter(p => p.orgId === action.payload.orgId)
            let newOrgMembers = action.payload.orgMembers
            oldOrgMembers.forEach(old => {
                const found = newOrgMembers.find(orgMember => orgMember.id === old.id)
                if (found) {
                    orgMembersAdapter.updateOne(state, {id: old.id, changes : found})
                    newOrgMembers = newOrgMembers.filter(orgMember => orgMember.id !== old.id)
                } else {
                    orgMembersAdapter.removeOne(state, old.id)
                }
            })
            orgMembersAdapter.upsertMany(state, newOrgMembers)
        }
    }
})

export const {
    orgMembersReset,
    orgMembersDelete,
    orgMembersUpsert,
    orgMemberUpdate,
    orgMembersSync,
} = orgMember.actions

export const orgMembersSelectors = orgMembersAdapter.getSelectors<RootState>(
    (state) => state.orgMembers
)
