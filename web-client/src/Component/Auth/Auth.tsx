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
import {useLocation} from "react-router-dom";
import {Header, SignIn} from "../index";
import Home from "../Home/Home";
import {useDispatch} from "react-redux";
import ApiClient from "../../Common/ApiClient";
import {isSignedIn} from "../../Redux";

const Auth : React.FC = () => {

    const location = useLocation()
    const redirectPath = encodeURI(location.pathname + location.search)
    const dispatch = useDispatch()
    const signedIn = isSignedIn()

    useEffect(() => {
        if (signedIn) {
            const apiClient = new ApiClient()
            apiClient.fetchMyAccountAndRedux(dispatch)
        }
    },[signedIn, dispatch])

    if (!signedIn) {
        if (process.env.NODE_ENV === "production") {
            if (process.env.REACT_APP_SAML_IDP_URL !== undefined) {
                window.location.assign(process.env.REACT_APP_SAML_IDP_URL)
            } else if (process.env.REACT_APP_GOOGLE_LOGIN_CLIENT_ID !== undefined) {
                window.location.assign("https://accounts.google.com/o/oauth2/auth?" +
                    "client_id=" + process.env.REACT_APP_GOOGLE_LOGIN_CLIENT_ID + "&" +
                    "redirect_uri=" + process.env.REACT_APP_API_SERVER_URL + "/login/google&" +
                    "scope=email&" +
                    "response_type=id_token&" +
                    "approval_prompt=force&" +
                    "flowName=GeneralOAuthFlow&" +
                    "response_mode=form_post")
            }
        }
    }

    return (
        <div>
            { signedIn && (
                <div>
                    <Header />
                    <Home />
                </div>
            )}
            { !signedIn && process.env.NODE_ENV === "development" &&
                <SignIn redirectPath={redirectPath} />
            }
            { !signedIn && process.env.NODE_ENV === "production" &&
                <div>
                    Redirecting...
                </div>
            }
        </div>
    )
}

export default Auth