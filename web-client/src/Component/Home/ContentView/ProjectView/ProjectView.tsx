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
import {Project} from "../../../../Redux";
import {IconButton, Tab, Tabs, Typography} from "@material-ui/core";
import {Book, Delete, Edit, SettingsApplications, Star} from "@material-ui/icons";
import {blue} from "@material-ui/core/colors";
import TabConfigsView from "./TabConfigsView/TabConfigsView";
import IconText from "../../../../Common/IconText";
import {DeleteProjectConfirmDialog, EditProjectDialog} from "../../Dialog";
import {useHistory} from "react-router-dom";
import TabProjectMemoView from "./TabMemoView/TabProjectMemoView";

interface Props {
    project: Project
}

const ProjectView: React.FC<Props> = ({project}) => {

    const [focusTab, setFocusTab] = useState(0)
    const [openEditDlgState, setOpenEditDlgState] = useState(false)
    const [openDeleteConfirmDlgState, setOpenDeleteConfirmDlgState] = useState(false)

    const history = useHistory()

    return (
        <div>
            <div style={{margin:"1em"}}>
                <div style={{display:"flex",justifyContent:"start",alignItems:"center"}}>
                    <Book fontSize="large" style={{color:blue[500],margin:"0 0.2em 0 0"}}/>
                    <Typography variant="h4" style={{width:"100%"}}>
                        { project && project.name}
                    </Typography>
                    <IconButton>
                        <Edit style={{color:blue[600]}} onClick={() => setOpenEditDlgState(true)}/>
                    </IconButton>
                    <IconButton>
                        <Delete color="secondary" onClick={() => setOpenDeleteConfirmDlgState(true)}/>
                    </IconButton>
                </div>
                <Typography variant="subtitle1" color="textSecondary">
                    { project && project.description}
                </Typography>
            </div>
            <Tabs
                value={focusTab}
                indicatorColor="primary"
                textColor="primary"
                style={{margin:"1em 0 0 0", borderBottom:"1px solid #aaaaaa"}}
            >
                <Tab
                    label={<IconText icon={<SettingsApplications />} text="コンフィグ" />}
                    onClick={() => {setFocusTab(0)}}
                />
                <Tab
                    label={<IconText icon={<Star />} text="メモ" />}
                    onClick={() => {setFocusTab(1)}}
                />
            </Tabs>
            { focusTab === 0 && project && (
                <TabConfigsView project={project} />
            )}
            { focusTab === 1 && project && (
                <TabProjectMemoView project={project} />
            )}
            { project && (
                <EditProjectDialog
                    open={openEditDlgState}
                    project={project}
                    onEditDone={(project) => {
                        history.push(`/orgs/${project.orgId}/projects/${project.id}`)
                    }}
                    onClose={() => setOpenEditDlgState(false)}
                />
            )}
            { project && (
                <DeleteProjectConfirmDialog
                    open={openDeleteConfirmDlgState}
                    project={project}
                    onDeleteDone={(project) => {
                        history.push(`/orgs/${project.orgId}`)
                    }}
                    onClose={() => setOpenDeleteConfirmDlgState(false)}
                />
            )}
        </div>
    )
}

export default ProjectView
