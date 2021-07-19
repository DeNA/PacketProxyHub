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

interface TreePane {
    width: string
}

const initialState: TreePane = {
    width: "300px",
}

export const treePaneSlice = createSlice({
    name: 'treePane',
    initialState,
    reducers: {
        treePaneReset: (state) => initialState,
        treePaneSetWidth(state, action) {
            state.width = action.payload
        }
    }
})

export const { treePaneReset, treePaneSetWidth } = treePaneSlice.actions
export const selectTreePaneWidth = (state: RootState) => state.treePane.width

export default treePaneSlice.reducer
