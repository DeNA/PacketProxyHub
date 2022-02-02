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

import {isSignedIn, signIn, signOut, selectMe, setMe, meReset, selectPacketProxyAccessToken} from "./Slice/Api"
import {selectTreePaneWidth, treePaneReset, treePaneSetWidth} from "./Slice/TreePane";
import {Org, orgsRemove, orgsReset, orgsSelectors, orgsUpdate, orgsUpsert, orgsSync, selectOrgByName} from "./Slice/Org"
import {
    initialProject,
    Project,
    projectsRemove,
    projectsSync,
    projectsReset,
    projectsSelectors,
    projectsUpdate,
    projectsUpsert,
    selectProjectByName
} from "./Slice/Project"
import {
    Config,
    configsRemove,
    configsReset,
    configsSelectors,
    configsUpsert,
    configUpdate,
    configsSync,
    initialConfig,
    selectConfigByName
} from "./Slice/Config"
import {
    initialOrgMember,
    OrgMember,
    orgMembersDelete,
    orgMembersReset,
    orgMembersSelectors,
    orgMembersUpsert,
    orgMemberUpdate,
    orgMembersSync,
} from "./Slice/OrgMember";
import {
    Account,
    accountsRemoveOne,
    accountsReset,
    accountsSelectors,
    accountsUpsert,
    accountUpdate,
} from "./Slice/Account";
import {
    initialTreeOpenState,
    TreeOpenState,
    treeOpenStatesSelectors,
    treeOpenStatesDelete,
    treeOpenStatesReset,
    treeOpenStatesUpsert,
    treeOpenStateUpdate,
    treeOpenStatesAdd,
} from "./Slice/TreeOpenState"
import {Dispatch} from "react";
import {Binary, initialBinary, binariesReset, binariesSelectors, binariesRemove, binariesSync, binariesUpsert} from "./Slice/Binary";
import {Snapshot, initialSnapshot, snapshotsReset, snapshotsSelectors, snapshotsRemove, snapshotsSync, snapshotsUpsert} from "./Slice/Snapshot";

export const resetRedux = (dispatch: Dispatch<any>) => {
    dispatch(meReset())
    dispatch(accountsReset())
    dispatch(treePaneReset())
    dispatch(configsReset())
    dispatch(orgMembersReset())
    dispatch(orgsReset())
    dispatch(projectsReset())
    dispatch(treeOpenStatesReset())
    dispatch(binariesReset())
    dispatch(snapshotsReset())
}

export type {
    Account,
    Org,
    Project,
    Config,
    OrgMember,
    Binary,
    Snapshot,
    TreeOpenState,
}

export {
    initialProject,
    initialConfig,
    initialOrgMember,
    initialBinary,
    initialSnapshot,
    initialTreeOpenState,
}

export {
    meReset,
    accountsReset,
    treePaneReset,
    configsReset,
    orgMembersReset,
    orgsReset,
    projectsReset,
    binariesReset,
    snapshotsReset,
    treeOpenStatesReset,
}

export {
    accountsSelectors,
    orgsSelectors,
    projectsSelectors,
    configsSelectors,
    orgMembersSelectors,
    binariesSelectors,
    snapshotsSelectors,
    treeOpenStatesSelectors,
}

export {
    isSignedIn,
    signIn,
    signOut,
    selectMe,
    selectPacketProxyAccessToken,
    setMe,
    accountsUpsert,
    accountUpdate,
    accountsRemoveOne,
    treePaneSetWidth,
    orgsUpdate,
    orgsUpsert,
    orgsRemove,
    orgsSync,
    projectsRemove,
    projectsSync,
    projectsUpdate,
    projectsUpsert,
    configUpdate,
    configsRemove,
    configsUpsert,
    configsSync,
    orgMembersDelete,
    orgMembersUpsert,
    orgMemberUpdate,
    orgMembersSync,
    binariesUpsert,
    binariesRemove,
    binariesSync,
    snapshotsUpsert,
    snapshotsRemove,
    snapshotsSync,
    treeOpenStateUpdate,
    treeOpenStatesUpsert,
    treeOpenStatesDelete,
    treeOpenStatesAdd,
}

export {
    selectOrgByName,
    selectProjectByName,
    selectConfigByName,
    selectTreePaneWidth,
}