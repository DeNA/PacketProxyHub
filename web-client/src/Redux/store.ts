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

import {Action, combineReducers, configureStore, getDefaultMiddleware, ThunkAction} from '@reduxjs/toolkit';
import apiReducer from './Slice/Api'
import treePaneReducer from './Slice/TreePane'
import {org} from "./Slice/Org";
import {project} from "./Slice/Project";
import {orgMember} from "./Slice/OrgMember";
import {account} from "./Slice/Account";
import {config} from "./Slice/Config";
import {treeOpenState} from "./Slice/TreeOpenState"

export const store = configureStore({
    reducer: combineReducers({
        api: apiReducer,
        treePane: treePaneReducer,
        accounts: account.reducer,
        orgs: org.reducer,
        orgMembers: orgMember.reducer,
        projects: project.reducer,
        configs: config.reducer,
        treeOpenStates: treeOpenState.reducer,
    }),
    middleware: [...getDefaultMiddleware()]
});

export type RootState = ReturnType<typeof store.getState>;
export type AppThunk<ReturnType = void> = ThunkAction<
  ReturnType,
  RootState,
  unknown,
  Action<string>
>;
