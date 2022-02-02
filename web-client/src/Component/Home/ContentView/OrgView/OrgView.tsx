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
import {Org} from "../../../../Redux";
import {useDispatch} from "react-redux";
import {IconButton, Tab, Tabs, Typography} from "@material-ui/core";
import {Book, Business, Delete, Edit, Group} from "@material-ui/icons";
import {blue, green} from "@material-ui/core/colors";
import TabProjectsView from "./TabProjectsView/TabProjectsView";
import TabOrgMembersView from "./TabOrgMembersView/TabOrgMembersView";
import IconText from "../../../../Common/IconText";
import EditOrgDialog from "../../Dialog/Org/EditOrgDialog";
import ApiClient from "../../../../Common/ApiClient";
import {useHistory} from "react-router-dom";
import DeleteOrgConfirmDialog from "../../Dialog/Org/DeleteOrgConfirmDialog";

interface Props {
    org: Org
}

const OrgView: React.FC<Props> = ({org}) => {

    const [focusTab, setFocusTab] = useState(0)
    const [openEditDlgState, setOpenEditDlgState] = useState(false)
    const [openDeleteConfirmDlgState, setOpenDeleteConfirmDlgState] = useState(false)

    const dispatch = useDispatch()
    const history = useHistory()
    const apiClient = new ApiClient()

    const handleOnEdit = (org: Org) : void => {
        (async () => {
            await apiClient.editOrgAndRedux(dispatch,org)
            history.push(`/orgs/${org.id}`)
        })()
    }

    const handleOnDelete = (org: Org) : void => {
        (async () => {
            await apiClient.deleteOrgAndRedux(dispatch, org)
            history.push("/")
        })()
    }

    return (
        <div>
            <div style={{margin:"1em"}}>
                <div style={{display:"flex",justifyContent:"start",alignItems:"center"}}>
                    <Business fontSize="large" style={{color:green[500],margin:"0 0.2em 0 0"}}/>
                    <Typography variant="h4" style={{width:"100%"}}>
                        { org && org.name}
                    </Typography>
                    <IconButton>
                        <Edit style={{color:blue[600]}} onClick={() => setOpenEditDlgState(true)}/>
                    </IconButton>
                    <IconButton>
                        <Delete color="secondary" onClick={() => setOpenDeleteConfirmDlgState(true)}/>
                    </IconButton>
                </div>
                <Typography variant="subtitle1" color="textSecondary">
                    { org && org.description}
                </Typography>
            </div>
            <Tabs
                value={focusTab}
                indicatorColor="primary"
                textColor="primary"
                style={{margin:"1em 0 0 0", borderBottom:"1px solid #aaaaaa"}}
            >
                <Tab
                    label={<IconText icon={<Book />} text="プロジェクト" />}
                    onClick={() => {setFocusTab(0)}}
                />
                <Tab
                    label={<IconText icon={<Group />} text="メンバー" />}
                    onClick={() => {setFocusTab(1)}}
                />
            </Tabs>
            { focusTab === 0 && org && (
                <TabProjectsView org={org} />
            )}
            { focusTab === 1 && org && (
                <TabOrgMembersView org={org} />
            )}
            { org && (
                <EditOrgDialog
                    open={openEditDlgState}
                    org={org}
                    onEdit={handleOnEdit}
                    onClose={() => setOpenEditDlgState(false)}
                />
            )}
            { org && (
                <DeleteOrgConfirmDialog
                    open={openDeleteConfirmDlgState}
                    org={org}
                    onDelete={handleOnDelete}
                    onClose={() => setOpenDeleteConfirmDlgState(false)}
                />
            )}
        </div>
    )
}

export default OrgView
