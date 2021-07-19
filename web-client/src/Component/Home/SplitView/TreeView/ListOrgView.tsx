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

import React from "react"
import {Config, Org, Project, projectsSelectors, treeOpenStatesSelectors, treeOpenStateUpdate} from "../../../../Redux";
import {Collapse, IconButton, List, ListItem} from "@material-ui/core";
import {Business, KeyboardArrowDown, KeyboardArrowRight} from "@material-ui/icons";
import {green} from "@material-ui/core/colors";
import ListProjectView from "./ListProjectView";
import {useDispatch, useSelector} from "react-redux";
import {RootState} from "../../../../Redux/store";

type Props = {
    selectedId: string
    org: Org
    onClickOrg: (org: Org) => void
    onClickProject: (org: Org, project: Project) => void
    onClickConfig: (org: Org, project: Project, config: Config) => void
}

const ListOrgView : React.FC<Props> = ({ selectedId, org, onClickOrg, onClickProject, onClickConfig }) => {

    const dispatch = useDispatch()

    const projects : Project[] = useSelector(projectsSelectors.selectAll).filter((project: Project) => {
        return project.orgId === org.id
    })
    const openStateObj = useSelector((state: RootState) => {
        return treeOpenStatesSelectors.selectById(state, org.id)
    })
    const openState = openStateObj !== undefined ?  openStateObj.openState : false

    return (
        <div>
            <ListItem
                button
                selected={org.id === selectedId}
                style={{display:"flex",justifyContent:"flex-start",alignItems:"center",width:"100%",padding:"2px"}}
                onClick={() => { onClickOrg(org) }}
            >
                {
                    <IconButton style={{padding:"0"}}>
                        { projects.length > 0 && openState && (
                            <KeyboardArrowDown
                                onClick={(event) => {
                                    dispatch(treeOpenStateUpdate({id: org.id, changes:{openState: !openState}}))
                                    event.stopPropagation()
                                }}
                            />
                        )}
                        { projects.length > 0 && !openState && (
                            <KeyboardArrowRight
                                onClick={(event) => {
                                    dispatch(treeOpenStateUpdate({id: org.id, changes: {openState: !openState}}))
                                    event.stopPropagation()
                                }}
                            />
                        )}
                        { projects.length === 0 && (
                            <span style={{marginLeft:"1em"}}></span>
                        )}
                    </IconButton>
                }
                <Business style={{ color:green[500] }} />
                <span style={{fontSize:"16px",width:"100%",textAlign:"left",margin:"0 .2em"}} >{org.name}</span>
            </ListItem>
            <Collapse in={projects.length > 0 && openState }>
                <List disablePadding>
                    {
                        projects.map((project: Project) => {
                            return <ListProjectView
                                key={project.id}
                                selectedId={selectedId}
                                project={project}
                                onClickProject={(project) => {
                                    onClickProject(org, project)
                                }}
                                onClickConfig={(project, config) => {
                                    onClickConfig(org, project, config)
                                }}
                            />
                        })
                    }
                </List>
            </Collapse>
        </div>
    )
}

export default ListOrgView
