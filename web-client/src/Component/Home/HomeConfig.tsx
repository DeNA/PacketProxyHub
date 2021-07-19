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

import React, {useEffect} from "react"
import {useDispatch, useSelector} from "react-redux";
import {
    Config,
    Org,
    Project,
    projectsSelectors,
    selectConfigByName,
    selectOrgByName,
    selectProjectByName,
    treeOpenStatesSelectors,
    treeOpenStateUpdate
} from "../../Redux";
import SplitView from "./SplitView/SplitView";
import {ConfigView} from "./ContentView";
import {RootState} from "../../Redux/store";

interface Props {
    orgName: string
    projectName: string
    configName: string
}

const HomeConfig : React.FC<Props> = ({orgName, projectName, configName}) => {

    const org : Org|undefined = useSelector((state: RootState) => {
        return selectOrgByName(state, orgName)
    })

    const projects : Project[] = useSelector((state: RootState) => {
        return selectProjectByName(state, projectName)
    })
    const filterdProjects : Project[] = projects.filter((project) => {
        if (org === undefined)
            return false
        return project.orgId === org.id
    })
    const project = filterdProjects.length > 0 ? filterdProjects[0] : undefined

    const configs : Config[] = useSelector((state: RootState) => {
        return selectConfigByName(state, configName)
    })
    const filterdConfigs : Config[] = configs.filter((config) => {
        if (org === undefined)
            return false
        if (project === undefined)
            return false
        return config.orgId === org.id && config.projectId === project.id
    })
    const config = filterdConfigs.length > 0 ? filterdConfigs[0] : undefined
    const dispatch = useDispatch()

    const parentProject = useSelector((state: RootState) => {
        if (!config) {
            return undefined
        }
        return projectsSelectors.selectById(state, config.projectId)
    })
    const openStateObj = useSelector((state: RootState) => {
        if (!parentProject) {
            return undefined
        }
        return treeOpenStatesSelectors.selectById(state, parentProject.id)
    })
    useEffect(() => {
        const openState = openStateObj !== undefined ?  openStateObj.openState : false
        if (parentProject && !openState) {
            dispatch(treeOpenStateUpdate({id: parentProject.id, changes: {openState: true}}))
        }
    }, [config])

    return (
        <div>
            { config && (
                <SplitView selectedId={config.id}>
                    <ConfigView config={config}/>
                </SplitView>
            )}
            { !config && (
                <p>
                    ロード中
                </p>
            )}
        </div>
    )
}

export default HomeConfig