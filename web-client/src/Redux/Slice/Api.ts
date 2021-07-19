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

import {createSlice} from '@reduxjs/toolkit'
import {RootState} from '../store'
import {Account, initialAccount} from "./Account";
import ApiClient from "../../Common/ApiClient";
import Cookies from "js-cookie";

interface ApiState {
    me: Account
}

const initialState: ApiState = {
    me: initialAccount,
}

export const isSignedIn = () : boolean => {
    return Cookies.get("packetproxyhub_session") !== undefined
}

export const signIn = async (params: any) : Promise<void> => {
    const apiServer = ApiClient.getApiServer()
    await fetch(`${apiServer}/login/debug`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=utf-8' },
        body: `name=${params.name}&mail=${params.mail}`,
        credentials:"include",
    })
}

export const signOut = async () : Promise<void> => {
    const apiServer = ApiClient.getApiServer()
    await fetch(`${apiServer}/logout`, {
        method: 'GET',
        credentials:"include",
    })
}

export const apiClientSlice = createSlice({
    name: 'api',
    initialState,
    reducers: {
        meReset: (state) => initialState,
        setMe(state, action) {
            state.me = action.payload
        }
    }
})

export const { meReset, setMe } = apiClientSlice.actions
export const selectMe = (state: RootState) => state.api.me
export const selectPacketProxyAccessToken = (state: RootState) => state.api.me.packetProxyAccessToken

export default apiClientSlice.reducer
