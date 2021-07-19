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
import {Config, configsSelectors, Project, treeOpenStatesSelectors, treeOpenStateUpdate} from "../../../../Redux";
import {Collapse, IconButton, List, ListItem} from "@material-ui/core";
import {Book, KeyboardArrowDown, KeyboardArrowRight} from "@material-ui/icons";
import {useDispatch, useSelector} from "react-redux";
import ListConfigView from "./ListConfigView";
import {blue} from "@material-ui/core/colors";
import {RootState} from "../../../../Redux/store";

interface Props {
    selectedId: string
    project: Project
    onClickProject: (project: Project) => void
    onClickConfig: (project: Project, config: Config) => void
}

const ListProjectView : React.FC<Props> = ({selectedId, project, onClickProject, onClickConfig}) => {

    const dispatch = useDispatch()
    const configs : Config[] = useSelector(configsSelectors.selectAll).filter((config: Config) => {
        return config.projectId === project.id
    })
    const openStateObj = useSelector((state: RootState) => {
        return treeOpenStatesSelectors.selectById(state, project.id)
    })
    const openState = openStateObj !== undefined ?  openStateObj.openState : false

    return (
        <div>
            <ListItem
                button
                selected={project.id === selectedId}
                style={{display:"flex",justifyContent:"flex-start",alignItems:"center",width:"100%",padding:"2px 2px 0 1.8em"}}
                onClick={() => { onClickProject(project) }}
            >
                {
                    <IconButton style={{padding:"0"}}>
                        { configs.length > 0 && openState && (
                            <KeyboardArrowDown
                                onClick={(event) => {
                                    dispatch(treeOpenStateUpdate({id: project.id, changes:{openState: !openState}}))
                                    event.stopPropagation()
                                }}
                            />
                        )}
                        { configs.length > 0 && !openState && (
                            <KeyboardArrowRight
                                onClick={(event) => {
                                    dispatch(treeOpenStateUpdate({id: project.id, changes:{openState: !openState}}))
                                    event.stopPropagation()
                                }}
                            />
                        )}
                        { configs.length === 0 && (
                            <span style={{marginLeft:"1em"}}></span>
                        )}
                    </IconButton>
                }
                <Book style={{color:blue[500]}}/>
                <span style={{fontSize:"15px",width:"100%",textAlign:"left",margin:"0 .2em"}}>{project.name}</span>
            </ListItem>
            <Collapse in={configs.length > 0 && openState}>
                <List disablePadding>
                    {
                        configs.map((config: Config) => {
                            return <ListConfigView
                                key={config.id}
                                selectedId={selectedId}
                                config={config}
                                onClickConfig={() => {
                                    onClickConfig(project, config)
                                }}
                            />
                        })
                    }
                </List>
            </Collapse>
        </div>
    )
}

export default ListProjectView
