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
import {useSelector} from "react-redux";
import {Org, selectOrgByName} from "../../Redux";
import SplitView from "./SplitView/SplitView";
import {OrgView} from "./ContentView";
import {RootState} from "../../Redux/store";

interface Props {
    orgName: string
}

const HomeOrg : React.FC<Props> = ({orgName}) => {

    const org : Org|undefined = useSelector((state: RootState) => {
        return selectOrgByName(state, orgName)
    })

    return (
        <div>
            { org && (
                <SplitView selectedId={org.id}>
                    <OrgView org={org}/>
                </SplitView>
            )}
            { !org && (
                <div>
                    ロード中
                </div>
            )}
        </div>
    )
}

export default HomeOrg