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

import React, {useState} from "react";
import {Button, Snackbar} from "@material-ui/core";
import {Config, configUpdate, selectPacketProxyAccessToken} from "../../../../../Redux";
import {useDispatch, useSelector} from "react-redux";
import {Alert} from "@material-ui/lab";
import PacketProxyClient from "../../../../../Common/PacketProxyClient";
import Code from "../../../../../Common/Code/Code"
import ApiClient from "../../../../../Common/ApiClient";

interface Props {
    config : Config
}

const TabPacketProxyConfView : React.FC<Props> = ({config}) => {

    const template = JSON.stringify({
        "listenPorts":[],
        "servers":[],
        "modifications":[],
        "sslPassThroughs":[]
    }, null, 4);

    const dispatch = useDispatch()
    const packetProxyAccessToken = useSelector(selectPacketProxyAccessToken)
    const packetProxyClient = new PacketProxyClient(packetProxyAccessToken)
    const apiClient = new ApiClient()

    const [openState, setOpenState] = useState(false)
    const [openErrorState, setOpenErrorState ] = useState(false)

    const handleOnChange = (editor: any, data: any, value: any) => {
        dispatch(configUpdate({id: config.id, changes: {...config, packetProxyConf:value}}))
    }

    const handleOnLoad = () => {
        (async() => {
            const packetProxyConfig = await packetProxyClient.getConfig()
            if (packetProxyConfig) {
                dispatch(configUpdate({id: config.id, changes: {...config, packetProxyConf: packetProxyConfig}}))
            } else {
                setOpenErrorState(true)
            }
        })()
    }

    const handleOnSave = () => {
        (async() => {
            await apiClient.editConfigAndRedux(dispatch, config)
            setOpenState(true)
        })()
    }

    const postConfig = () => {
        (async() => {
            const ret = await packetProxyClient.postConfig(config.packetProxyConf)
            if (ret === undefined) {
                setOpenErrorState(true)
            }
        })()
    }

    return (
        <div style={{width:"90%",margin:"0 auto",marginTop:"2em"}}>
            <div style={{display:"flex"}}>
                <Button style={{marginRight:"auto"}} variant="outlined" size="small" color="secondary" onClick={postConfig} >
                    PacketProxyにこの設定を送信
                </Button>
                <Button variant="contained" size="small" color="primary" onClick={handleOnLoad} >
                    PacketProxyから設定を取得
                </Button>
                <Button style={{marginLeft:"1em"}} variant="contained" size="small" color="primary" onClick={handleOnSave} >
                    保存
                </Button>
            </div>
            <div style={{margin:"1em 0"}}>
                <Code
                    value={config.packetProxyConf.length > 0 ? config.packetProxyConf : template }
                    mode="javascript"
                    onChange={handleOnChange}
                />
            </div>
            <Snackbar anchorOrigin={{vertical:"top",horizontal:"center"}} open={openState} autoHideDuration={2000} onClose={() => {setOpenState(false)}}>
                <Alert onClose={() => {setOpenState(false)}} severity="success">
                    <p>
                        保存しました
                    </p>
                </Alert>
            </Snackbar>
            <Snackbar anchorOrigin={{vertical:"top",horizontal:"center"}} open={openErrorState} autoHideDuration={10000} onClose={() => {setOpenErrorState(false)}}>
                <Alert onClose={() => {setOpenErrorState(false)}} severity="error">
                    <div>
                        PacketProxyにアクセスできません。<br/>
                        <p>
                        下記について確認してください。<br/>
                        </p>
                        <ul>
                            <li>PacketProxyが起動していること</li>
                            <li>PacketProxyの「Import/Export Configs」が Enabled になっていること</li>
                            <li>PacketProxyのAccessTokenを、PacketProxyHubのアカウント設定で登録していること</li>
                        </ul>
                    </div>
                </Alert>
            </Snackbar>
        </div>
    )
}

export default TabPacketProxyConfView
