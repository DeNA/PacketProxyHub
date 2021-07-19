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
import {Redirect} from "react-router-dom";
import {useSelector} from "react-redux";
import {configsSelectors, orgsSelectors, projectsSelectors} from "../../Redux";
import {RootState} from "../../Redux/store";

interface Props {
    orgId: string
    projectId?: string
    configId?: string
}

const HomeRedirector : React.FC<Props> = ({orgId, projectId= "", configId = ""}) => {

    let path = "";

    const org = useSelector((state: RootState) => {
        return orgsSelectors.selectById(state, orgId)
    })
    if (org)
        path = `/${encodeURIComponent(org.name)}`

    const project = useSelector((state: RootState) => {
        return projectsSelectors.selectById(state, projectId)
    })
    if (project)
        path = `${path}/${encodeURIComponent(project.name)}`

    const config = useSelector((state: RootState) => {
        return configsSelectors.selectById(state, configId)
    })
    if (config)
        path = `${path}/${encodeURIComponent(config.name)}`

    return (
        <Redirect to={path} />
    )
}

export default HomeRedirector