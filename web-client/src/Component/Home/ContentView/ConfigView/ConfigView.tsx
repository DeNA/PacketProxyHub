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

import React, {useState} from "react"
import {Config} from "../../../../Redux";
import {IconButton, Tab, Tabs, Typography} from "@material-ui/core";
import {Delete, Edit, SettingsApplications} from "@material-ui/icons";
import {blue, purple} from "@material-ui/core/colors";
import TabPacketProxyConfView from "./TabPacketProxyConfView/TabPacketProxyConfView";
import TabPfConfView from "./TabPfConfView/TabPfConfView";
import TabMemoView from "./TabMemoView/TabMemoView";
import {DeleteConfigConfirmDialog, EditConfigDialog} from "../../Dialog";
import {useHistory} from "react-router-dom";

interface Props {
    config: Config
}

const ConfigView: React.FC<Props> = ({config}) => {

    const [focusTab, setFocusTab] = useState(0)
    const [openEditDlgState, setOpenEditDlgState] = useState(false)
    const [openDelteDlgState, setOpenDeleteDlgState] = useState(false)

    const history = useHistory()

    return (
        <div>
            <div style={{margin:"1em"}}>
                <div style={{display:"flex",justifyContent:"start",alignItems:"center"}}>
                    <SettingsApplications fontSize="large" style={{color:purple[500],margin:"0 0.2em 0 0"}}/>
                    <Typography variant="h4" style={{width:"100%"}}>
                        { config.name }
                    </Typography>
                    <IconButton>
                        <Edit style={{color:blue[600]}} onClick={() => setOpenEditDlgState(true)}/>
                    </IconButton>
                    <IconButton>
                        <Delete color="secondary" onClick={() => setOpenDeleteDlgState(true)}/>
                    </IconButton>
                </div>
                <Typography variant="subtitle1" color="textSecondary">
                    { config.description }
                </Typography>
            </div>
            <Tabs
                value={focusTab}
                indicatorColor="primary"
                textColor="primary"
                style={{margin:"1em 0 0 0", borderBottom:"1px solid #aaaaaa"}}
            >
                <Tab
                    label="PacketProxy 設定"
                    onClick={() => {setFocusTab(0)}}
                />
                <Tab
                    label="pf.conf 設定"
                    onClick={() => {setFocusTab(1)}}
                />
                <Tab
                    label="メモ"
                    onClick={() => {setFocusTab(2)}}
                />
            </Tabs>
            { focusTab === 0 && config && (
                <TabPacketProxyConfView config={config}/>
            )}
            { focusTab === 1 && config && (
                <TabPfConfView config={config}/>
            )}
            { focusTab === 2 && config && (
                <TabMemoView config={config}/>
            )}
            <EditConfigDialog
                open={openEditDlgState}
                config={config}
                onEditDone={(newConfig) => {
                    history.push(`/orgs/${newConfig.orgId}/projects/${newConfig.projectId}/configs/${newConfig.id}`)
                }}
                onError={() => {
                    history.push(`/orgs/${config.orgId}/projects/${config.projectId}`)
                }}
                onClose={() => setOpenEditDlgState(false)}
            />
            <DeleteConfigConfirmDialog
                open={openDelteDlgState}
                config={config}
                onDeleteDone={() => {
                    history.push(`/orgs/${config.orgId}/projects/${config.projectId}`)
                }}
                onError={() => {
                    history.push(`/orgs/${config.orgId}/projects/${config.projectId}`)
                }}
                onClose={() => setOpenDeleteDlgState(false)}
            />
        </div>
    )
}

export default ConfigView
