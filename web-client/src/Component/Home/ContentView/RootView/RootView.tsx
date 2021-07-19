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
import {useDispatch} from "react-redux";
import ApiClient from "../../../../Common/ApiClient";

const RootView : React.FC = () => {

    const dispatch = useDispatch()
    const apiClient = new ApiClient()

    useEffect(() => {
        (async() => {
            await apiClient.fetchOrgsAndRedux(dispatch)
        })()
    },[])

    return (
        <div>
        </div>
    )
}

export default RootView