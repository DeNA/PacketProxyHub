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
import {Route, Switch} from "react-router-dom";
import {useDispatch} from "react-redux";
import SplitView from "./SplitView/SplitView";
import HomeOrg from "./HomeOrg";
import HomeProject from "./HomeProject";
import HomeConfig from "./HomeConfig";
import HomeRedirector from "./HomeRedirector";
import {RootView} from "./ContentView";
import ApiClient from "../../Common/ApiClient";

const Home : React.FC = () => {

    const dispatch = useDispatch()
    const apiClient = new ApiClient()

    useEffect(() => {
        (async () => {
            await apiClient.fetchAllAndRedux(dispatch)
        })()
    }, [])

    return (
        <div>
            <Switch>
                <Route exact path={"/orgs/:orgId/projects/:projectId/configs/:configId"} render={({match})=>
                    <HomeRedirector
                        orgId={match.params.orgId}
                        projectId={match.params.projectId}
                        configId={match.params.configId}
                    />
                }/>
                <Route exact path={"/orgs/:orgId/projects/:projectId"} render={({match}) =>
                    <HomeRedirector
                        orgId={match.params.orgId}
                        projectId={match.params.projectId}
                    />
                }/>
                <Route exact path={"/orgs/:orgId"} render={({match}) =>
                    <HomeRedirector
                        orgId={match.params.orgId}
                    />
                }/>
                <Route exact path={"/:orgName"} render={({match}) =>
                    <HomeOrg orgName={decodeURIComponent(match.params.orgName)}/>
                }/>
                <Route exact path={"/:orgName/:projectName"} render={({match}) =>
                    <HomeProject
                        orgName={decodeURIComponent(match.params.orgName)}
                        projectName={decodeURIComponent(match.params.projectName)}
                    />
                }/>
                <Route exact path={"/:orgName/:projectName/:configName"} render={({match}) =>
                    <HomeConfig
                        orgName={decodeURIComponent(match.params.orgName)}
                        projectName={decodeURIComponent(match.params.projectName)}
                        configName={decodeURIComponent(match.params.configName)}
                    />
                }/>
                <Route path="(/)?" render={({match}) =>
                    <SplitView selectedId="" >
                        <RootView />
                    </SplitView>
                }/>
            </Switch>
        </div>
    )
}

export default Home