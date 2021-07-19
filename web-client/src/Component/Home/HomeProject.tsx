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
    orgsSelectors,
    Project,
    projectsSelectors,
    selectOrgByName,
    selectProjectByName,
    treeOpenStatesSelectors,
    treeOpenStateUpdate
} from "../../Redux";
import SplitView from "./SplitView/SplitView";
import {ProjectView} from "./ContentView";
import {RootState} from "../../Redux/store";
import {useHistory} from "react-router-dom";

interface Props {
    orgName: string
    projectName: string
}

const HomeProject : React.FC<Props> = ({orgName, projectName}) => {

    const org = useSelector((state: RootState) => {
        return selectOrgByName(state, orgName)
    })

    const projects : Project[] = useSelector((state: RootState) => {
        return selectProjectByName(state, projectName)
    })

    const filtered : Project[] = projects.filter((project) => {
        if (org === undefined)
            return false
        return project.orgId === org.id
    })

    const project = filtered.length > 0 ? filtered[0] : undefined
    const dispatch = useDispatch()

    const parentOrg = useSelector((state: RootState) => {
        if (!project) {
            return undefined
        }
        return orgsSelectors.selectById(state, project.orgId)
    })
    const openStateObj = useSelector((state: RootState) => {
        if (!parentOrg) {
            return undefined
        }
        return treeOpenStatesSelectors.selectById(state, parentOrg.id)
    })
    useEffect(() => {
        const openState = openStateObj !== undefined ? openStateObj.openState : false
        if (parentOrg && !openState) {
            dispatch(treeOpenStateUpdate({id: parentOrg.id, changes: {openState: true}}))
        }
    }, [project])

    return (
        <div>
            { project && (
                <SplitView selectedId={project.id}>
                    <ProjectView project={project}/>
                </SplitView>
            )}
            { !project && (
                <p>
                    ロード中
                </p>
            )}
        </div>
    )
}

export default HomeProject