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

export interface Account {
    id: string
    name: string
    mail: string
    packetProxyAccessToken: string
}

export const initialAccount : Account = {
    id: "",
    name: "",
    mail: "",
    packetProxyAccessToken: "",
}

export const accountsAdapter = createEntityAdapter<Account>({
    selectId: (account) => account.id,
    sortComparer: (a, b) => a.name.localeCompare(b.name),
})

export const account = createSlice({
    name: "accounts",
    initialState: accountsAdapter.getInitialState(),
    reducers: {
        accountsReset: (state) => accountsAdapter.getInitialState(),
        accountsUpsert: accountsAdapter.upsertMany,
        accountsRemoveOne: accountsAdapter.removeOne,
        accountUpdate: accountsAdapter.updateOne,
    }
})

export const {
    accountsReset,
    accountsUpsert,
    accountsRemoveOne,
    accountUpdate,
} = account.actions

export const accountsSelectors = accountsAdapter.getSelectors<RootState>(
    (state) => state.accounts
)
