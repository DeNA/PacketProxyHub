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

import React, {useEffect, useState} from "react"
import {Config, Org, Project} from "../../../../Redux";
import {IconButton, Tab, Tabs, Typography} from "@material-ui/core";
import {Delete, Edit, SettingsApplications} from "@material-ui/icons";
import {blue, purple} from "@material-ui/core/colors";
import TabPacketProxyConfView from "./TabPacketProxyConfView/TabPacketProxyConfView";
import TabPfConfView from "./TabPfConfView/TabPfConfView";
import TabMemoView from "./TabMemoView/TabMemoView";
import {DeleteConfigConfirmDialog, EditConfigDialog} from "../../Dialog";
import {useHistory} from "react-router-dom";
import TabBinaryView from "./TabBinaryView/TabBinaryView";
import TabSnapshotView from "./TabSnapshotView/TabSnapshotView";
import TabFridaScriptView from "./TabFridaScriptView/TabFridaScriptView";

interface Props {
    org: Org
    project: Project
    config: Config
    tab?: string
}

interface Tab {
    index: number
    name: string
}

const tabList:Tab[] = [
    { "index": 0, "name" : "packetproxy" },
    { "index": 1, "name" : "pf" },
    { "index": 2, "name" : "frida" },
    { "index": 3, "name" : "binaries" },
    { "index": 4, "name" : "snapshots" },
    { "index": 5, "name" : "memo" }
]

const TabNameToIndex = (name:string|undefined) : number => {
    if (name === undefined) {
        return 0
    }
    const tab:Tab|undefined = tabList.find((tab:Tab) => tab.name === name)
    if (tab === undefined) {
        return 0
    }
    return tab.index
}

const IndexToTabName = (index:number) : string => {
    const tab:Tab|undefined = tabList.find((tab:Tab) => tab.index === index)
    if (tab === undefined) {
        return "packetproxy"
    }
    return tab.name
}

const ConfigView: React.FC<Props> = ({org, project, config, tab}) => {

    const [focusTab, setFocusTab] = useState(TabNameToIndex(tab))
    const [openEditDlgState, setOpenEditDlgState] = useState(false)
    const [openDelteDlgState, setOpenDeleteDlgState] = useState(false)

    const history = useHistory()

    useEffect(() => {
        history.replace(`/${encodeURIComponent(org.name)}/${encodeURIComponent(project.name)}/${encodeURIComponent(config.name)}/${IndexToTabName(focusTab)}`)
    }, [history, org, project, config, focusTab, tab])

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
                    onClick={() => {
                        setFocusTab(TabNameToIndex("packetproxy"))
                    }}
                />
                <Tab
                    label="pf.conf 設定"
                    onClick={() => {
                        setFocusTab(TabNameToIndex("pf"))
                    }}
                />
                <Tab
                    label="Fridaスクリプト"
                    onClick={() => {
                        setFocusTab(TabNameToIndex("frida"))
                    }}
                />
                <Tab
                    label="バイナリ"
                    onClick={() => {
                        setFocusTab(TabNameToIndex("binaries"))
                    }}
                />
                <Tab
                    label="スナップショット"
                    onClick={() => {
                        setFocusTab(TabNameToIndex("snapshots"))
                    }}
                />
                <Tab
                    label="メモ"
                    onClick={() => {
                        setFocusTab(TabNameToIndex("memo"))
                    }}
                />
            </Tabs>
            { focusTab === TabNameToIndex("packetproxy") && config && (
                <TabPacketProxyConfView config={config}/>
            )}
            { focusTab === TabNameToIndex("pf") && config && (
                <TabPfConfView config={config}/>
            )}
            { focusTab === TabNameToIndex("frida") && config && (
                <TabFridaScriptView config={config}/>
            )}
            { focusTab === TabNameToIndex("binaries") && config && (
                <TabBinaryView config={config}/>
            )}
            { focusTab === TabNameToIndex("snapshots") && config && (
                <TabSnapshotView config={config}/>
            )}
            { focusTab === TabNameToIndex("memo") && config && (
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
