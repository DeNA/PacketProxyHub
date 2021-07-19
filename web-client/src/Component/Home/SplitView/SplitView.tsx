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

import React from 'react'
import SplitPane from "react-split-pane";
import {ListOrgsView} from "./TreeView";
import {useHistory} from "react-router-dom";
import {Config, Org, Project, selectTreePaneWidth, treePaneSetWidth} from "../../../Redux";
import {useDispatch, useSelector} from "react-redux";
import ApiClient from "../../../Common/ApiClient";

var Pane = require("react-split-pane/lib/Pane");

interface Props {
    selectedId: string
}

const SplitView : React.FC<Props> = ({selectedId, children}) => {

    const history = useHistory()
    const dispatch = useDispatch()
    const width = useSelector(selectTreePaneWidth)
    const apiClient = new ApiClient()

    return (
        <div>
            <SplitPane onChange={(size: any) => {dispatch(treePaneSetWidth(size[0]))}} >
                <Pane minSize="200px" size={width} maxSize="400px" >
                    <div style={{height:"calc(100vh - 48px)",overflowY:"auto"}}>
                        <ListOrgsView
                            selectedId={selectedId}
                            onClickOrg={async (org: Org) => {
                                await apiClient.fetchOrgsAndRedux(dispatch)
                                history.push(`/${encodeURIComponent(org.name)}`)
                            }}
                            onClickProject={async (org:Org, project: Project) => {
                                const ret = await apiClient.fetchProjectsAndRedux(dispatch, org.id)
                                if (ret === undefined) {
                                    history.push(`/`)
                                } else {
                                    history.push(`/${encodeURIComponent(org.name)}/${encodeURIComponent(project.name)}`)
                                }
                            }}
                            onClickConfig={async (org:Org, project:Project, config: Config) => {
                                const ret = await apiClient.fetchConfigsAndRedux(dispatch, project)
                                if (ret === undefined) {
                                    history.push(`/${encodeURIComponent(org.name)}`)
                                } else {
                                    history.push(`/${encodeURIComponent(org.name)}/${encodeURIComponent(project.name)}/${encodeURIComponent(config.name)}`)
                                }
                            }}
                        />
                    </div>
                </Pane>
                <div style={{height:"calc(100vh - 48px)",overflow:"auto"}}>
                    { children }
                </div>
            </SplitPane>
        </div>
    )
}

export default SplitView