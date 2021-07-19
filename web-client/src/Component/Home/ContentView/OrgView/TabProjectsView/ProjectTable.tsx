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

import React, {useEffect} from "react";
import {Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow} from "@material-ui/core";
import {Org, Project, projectsSelectors} from "../../../../../Redux";
import {useDispatch, useSelector} from "react-redux";
import ProjectTableRow from "./ProjectTableRow";
import ApiClient from "../../../../../Common/ApiClient";
import {useHistory} from "react-router-dom";

interface Props {
    org: Org
}

const ProjectTable : React.FC<Props> = ({org}) => {

    const projects : Project[] = useSelector(projectsSelectors.selectAll).filter((project: Project) => {
        return project.orgId === org.id
    })

    const dispatch = useDispatch()
    const apiClient = new ApiClient()
    const history = useHistory()

    useEffect(() => {
        const projects = apiClient.fetchProjectsAndRedux(dispatch, org.id)
        if (projects === undefined) {
            history.push(`/orgs/${org.id}`)
        }
    }, [org.id])

    return (
        <TableContainer component={Paper} style={{backgroundColor:"#FFFFFF"}} >
            <Table aria-label="simple table">
                <TableHead>
                    <TableRow>
                        <TableCell style={{fontWeight:"bold"}}>プロジェクト名</TableCell>
                        <TableCell style={{fontWeight:"bold"}}>概要</TableCell>
                        <TableCell style={{fontWeight:"bold"}}>更新時刻</TableCell>
                        <TableCell style={{fontWeight:"bold",padding:"0",width:"5%"}} align="center">編集</TableCell>
                        <TableCell style={{fontWeight:"bold",padding:"0",width:"5%"}} align="center">削除</TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {
                        projects.map((project) => {
                            return (
                                <ProjectTableRow key={project.id} project={project} />
                            )
                        })
                    }
                </TableBody>
            </Table>
        </TableContainer>
    )
}

export default ProjectTable