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

import React, {useEffect, useState} from "react"
import {Button, TextField} from "@material-ui/core";
import {isSignedIn, signIn} from "../../Redux";
import {useHistory} from "react-router-dom";

interface Props {
    redirectPath: string
}

const SignIn : React.FC<Props> = ({redirectPath}) => {

    const history = useHistory()

    const [name, setName] = useState("")
    const [mail, setMail] = useState("")
    const signedIn = isSignedIn()

    useEffect(() => {
        if (signedIn) {
            history.push(redirectPath)
        }
    }, [signedIn, history, redirectPath])

    const handleOnChangeName = (event: React.ChangeEvent<HTMLInputElement>) => {
        setName(event.currentTarget.value)
    }

    const handleOnChangeMail = (event: React.ChangeEvent<HTMLInputElement>) => {
        setMail(event.currentTarget.value)
    }

    const handleSignIn = (name: string, mail: string) => {
        (async() => {
            await signIn({name, mail})
            history.push(redirectPath)
        })()
    }

    const googleLogin = () => {
        window.location.assign("https://accounts.google.com/o/oauth2/auth?" +
            "client_id=" + process.env.REACT_APP_GOOGLE_LOGIN_CLIENT_ID + "&" +
            "redirect_uri=http://localhost:1234/login/google&" +
            "scope=email&" +
            "response_type=id_token&" +
            "approval_prompt=force&" +
            "flowName=GeneralOAuthFlow&" +
            "response_mode=form_post")
    }

    return (
        <div>
            <h1 style={{textAlign:"center"}}>
                デバッグ用サインインページ
            </h1>
            <div style={{textAlign:"center"}}>
                <TextField label="name" onChange={handleOnChangeName}/>
            </div>
            <div style={{textAlign:"center"}}>
                <TextField label="mail" onChange={handleOnChangeMail}/>
            </div>
            <div style={{textAlign:"center"}}>
                <Button variant="contained" color="primary" onClick={() => handleSignIn(name, mail) }>サインイン</Button>
            </div>
            <div style={{textAlign:"center"}}>
                <Button variant="outlined" color="primary" style={{margin:"0 1em 0 0"}} onClick={() => handleSignIn("aaa", "aaa@example.com")}>aaa@example.comでサインイン</Button>
                <Button variant="outlined" color="primary" style={{margin:"0 0 0 1em"}} onClick={() => handleSignIn("bbb", "bbb@example.com")}>bbb@example.comでサインイン</Button>
            </div>
            <div style={{textAlign:"center"}}>
                <Button variant="contained" color="secondary" style={{margin:"2em 0 0 0"}} onClick={() => googleLogin()} >Googleログイン</Button>
            </div>
        </div>
    )
}

export default SignIn