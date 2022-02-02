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

import React, {useEffect, useState} from "react";
import {
    Button,
    IconButton,
    Link,
    Paper,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow
} from "@material-ui/core";
import {Config, configsSelectors, initialConfig, Project} from "../../../../../Redux";
import {Add, Delete, Edit, SettingsApplications} from "@material-ui/icons";
import {blue, purple} from "@material-ui/core/colors";
import {useDispatch, useSelector} from "react-redux";
import PHDate from "../../../../../Common/PHDate";
import IconText from "../../../../../Common/IconText";
import {useHistory} from "react-router-dom";
import AccountName from "../../../../../Common/AccountName";
import CreateConfigDialog from "../../../Dialog/Config/CreateConfigDialog";
import {DeleteConfigConfirmDialog, EditConfigDialog} from "../../../Dialog";
import ApiClient from "../../../../../Common/ApiClient";

interface Props {
    project: Project
}

const TabConfigsView : React.FC<Props> = ({project}) => {

    const [openDlgState, setOpenDlgState] = useState(false)

    const [deleteConfig, setDeleteConfig] = useState<Config>(initialConfig)
    const [openDeleteDlgState, setOpenDeleteDlgState] = useState(false)

    const [editConfig, setEditConfig] = useState<Config>(initialConfig)
    const [openEditDlgState, setOpenEditDlgState] = useState(false)

    const configs : Config[] = useSelector(configsSelectors.selectAll).filter((config: Config) => {
        return config.projectId === project.id
    })

    const history = useHistory()
    const dispatch = useDispatch()
    const apiClient = new ApiClient()

    useEffect(() => {
        const configs = apiClient.fetchConfigsAndRedux(dispatch, project)
        if (configs === undefined) {
            history.push(`/orgs/${project.orgId}/projects/${project.id}`)
        }
    }, [project.id])

    return (
        <div style={{width:"90%",margin:"0 auto",marginTop:"2em"}}>
            <div>
                <Button variant="outlined" size="small" color="primary" startIcon={<Add />} onClick={() => setOpenDlgState(true)} >
                    新規コンフィグ
                </Button>
            </div>
            <div style={{margin:"1em 0"}}>
                <TableContainer component={Paper} style={{backgroundColor:"#FFFFFF"}} >
                    <Table aria-label="simple table">
                        <TableHead>
                            <TableRow>
                                <TableCell style={{fontWeight:"bold"}}>コンフィグ名</TableCell>
                                <TableCell style={{fontWeight:"bold"}} width="auto">概要</TableCell>
                                <TableCell style={{fontWeight:"bold"}}>更新者</TableCell>
                                <TableCell style={{fontWeight:"bold"}}>更新時刻</TableCell>
                                <TableCell style={{fontWeight:"bold"}} align="center" width="20px">編集</TableCell>
                                <TableCell style={{fontWeight:"bold"}} align="center" width="20px">削除</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {
                                configs.map((config) => {
                                    return (
                                        <TableRow key={config.id}>
                                            <TableCell>
                                                <IconText
                                                    icon={<SettingsApplications style={{color:purple[500]}}/>}
                                                    text={
                                                        <Link
                                                            href="#"
                                                            color="inherit"
                                                            onClick={() => history.push(`/orgs/${config.orgId}/projects/${config.projectId}/configs/${config.id}`)}
                                                        >
                                                            {config.name}
                                                        </Link>
                                                    }
                                                />
                                            </TableCell>
                                            <TableCell width="auto">
                                                { config.description }
                                            </TableCell>
                                            <TableCell>
                                                <AccountName accountId={config.accountId} />
                                            </TableCell>
                                            <TableCell>
                                                {
                                                    new PHDate(config.updatedAt && config.updatedAt).toString()
                                                }
                                            </TableCell>
                                            <TableCell align="center" width="20px">
                                                <IconButton
                                                    onClick={() => {
                                                        setEditConfig(config)
                                                        setOpenEditDlgState(true)
                                                    }}
                                                >
                                                    <Edit style={{color:blue[600]}}/>
                                                </IconButton>
                                            </TableCell>
                                            <TableCell align="center" width="20px">
                                                <IconButton
                                                    onClick={() => {
                                                        setDeleteConfig(config)
                                                        setOpenDeleteDlgState(true)
                                                    }}
                                                >
                                                    <Delete color="secondary"/>
                                                </IconButton>
                                            </TableCell>
                                        </TableRow>
                                    )
                                })
                            }
                        </TableBody>
                    </Table>
                </TableContainer>
            </div>
            <CreateConfigDialog
                project={project}
                open={openDlgState}
                onClose={() => {setOpenDlgState(false)}}
            />
            <DeleteConfigConfirmDialog
                open={openDeleteDlgState}
                config={deleteConfig}
                onError={() => {
                    history.push(`/orgs/${project.orgId}/projects/${project.id}`)
                }}
                onClose={() => setOpenDeleteDlgState(false)}
            />
            <EditConfigDialog
                open={openEditDlgState}
                config={editConfig}
                onError={() => {
                    history.push(`/orgs/${project.orgId}/projects/${project.id}`)
                }}
                onClose={() => setOpenEditDlgState(false)}
            />
        </div>
    )
}

export default TabConfigsView