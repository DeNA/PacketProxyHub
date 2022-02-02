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

import React, {useState} from "react";
import {IconButton, Link, TableCell, TableRow} from "@material-ui/core";
import {Project} from "../../../../../Redux";
import {Book, Delete, Edit} from "@material-ui/icons";
import PHDate from "../../../../../Common/PHDate";
import IconText from "../../../../../Common/IconText";
import DeleteProjectConfirmDialog from "../../../Dialog/Project/DeleteProjectConfirmDialog";
import {useHistory} from "react-router-dom";
import {EditProjectDialog} from "../../../Dialog";
import {blue} from "@material-ui/core/colors";

interface Props {
    project: Project
}

const ContentOrgProjectsView : React.FC<Props> = ({project}) => {

    const [openEditDlgState, setOpenEditDlgState] = useState(false)
    const [openDeleteDlgState, setOpenDeleteDlgState] = useState(false)

    const history = useHistory()

    return (
        <TableRow key={project.id}>
            <TableCell>
                <IconText
                    icon={<Book color="primary" />}
                    text={
                        <Link
                            color="inherit"
                            href="#"
                            onClick={() => {
                                history.push(`/orgs/${project.orgId}/projects/${project.id}`)
                            }}
                        >
                            {project && project.name}
                        </Link>
                    }
                />
            </TableCell>
            <TableCell>{project && project.description}</TableCell>
            <TableCell>
                {
                    new PHDate(project && project.updatedAt).toString()
                }
            </TableCell>
            <TableCell align="center" style={{padding:"0",width:"5%"}}>
                <IconButton
                    onClick={() => {
                        setOpenEditDlgState(true)
                    }}
                >
                    <Edit style={{color:blue[600]}}/>
                </IconButton>
            </TableCell>
            <TableCell align="center" style={{padding:"0",width:"5%"}}>
                <IconButton
                    onClick={() => {
                        setOpenDeleteDlgState(true)
                    }}
                >
                    <Delete color="secondary"/>
                </IconButton>
            </TableCell>
            <EditProjectDialog
                open={openEditDlgState}
                project={project}
                onClose={() => setOpenEditDlgState(false)}
            />
            <DeleteProjectConfirmDialog
                open={openDeleteDlgState}
                project={project}
                onClose={() => setOpenDeleteDlgState(false)}
            />
        </TableRow>
    )
}

export default ContentOrgProjectsView