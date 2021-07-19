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
import {Button, Dialog, DialogTitle, Grid, List, ListItem, Snackbar, TextField} from "@material-ui/core";
import {useDispatch, useSelector} from "react-redux";
import {accountsRemoveOne, accountsUpsert, selectMe, setMe} from "../../../../Redux";
import ApiClient from "../../../../Common/ApiClient";
import {Alert} from "@material-ui/lab";

interface Props {
    open: boolean
    onClose: () => void
}

const EditAccountDialog : React.FC<Props> = ({open, onClose}) => {

    const [name, setName] = useState("")
    const [mail, setMail] = useState("")
    const [packetProxyAccessToken, setPacketProxyAccessToken] = useState("")
    const [openSnackState, setOpenSnackState] = useState(false)

    const dispatch = useDispatch()
    const myAccount = useSelector(selectMe)
    const apiClient : ApiClient = new ApiClient()

    useEffect(() => {
        setName(myAccount.name)
        setMail(myAccount.mail)
        setPacketProxyAccessToken(myAccount.packetProxyAccessToken)
    }, [myAccount])

    const handleOnSave = () => {
        (async() => {
            await apiClient.editMyAccount({
                id: myAccount.id,
                name: name,
                mail: myAccount.mail,
                packetProxyAccessToken: packetProxyAccessToken,
            })
            const account = await apiClient.fetchMyAccount()
            dispatch(accountsRemoveOne(myAccount.id))
            dispatch(accountsUpsert([account]))
            dispatch(setMe(account))
            setOpenSnackState(true)
            onClose()
        })()
    }

    return (
        <div>
            <Dialog onClose={onClose} open={open} fullWidth={true} maxWidth={'sm'} >
                <DialogTitle>アカウントの編集</DialogTitle>
                <List >
                    <ListItem>
                        <TextField
                            fullWidth={true}
                            id="name"
                            defaultValue={name}
                            label="アカウント名"
                            onChange={(event: React.ChangeEvent<HTMLInputElement>) =>
                                setName(event.currentTarget.value)
                            }
                        />
                    </ListItem>
                    <ListItem>
                        <TextField
                            disabled={true}
                            fullWidth={true}
                            id="mail"
                            defaultValue={mail}
                            label="メールアドレス"
                        />
                    </ListItem>
                    <ListItem>
                        <TextField
                            fullWidth={true}
                            id="packetProxyAccessToken"
                            defaultValue={packetProxyAccessToken}
                            label="PacketProxyのアクセストークン"
                            onChange={(event: React.ChangeEvent<HTMLInputElement>) =>
                                setPacketProxyAccessToken(event.currentTarget.value)
                            }
                        />
                    </ListItem>
                </List>
                <div style={{padding:20}}>
                    <Grid container justify="center">
                        <Grid item>
                            <Button onClick={onClose} color='primary'>キャンセル</Button>
                        </Grid>
                        <Grid item>
                            <Button variant="contained" onClick={handleOnSave} color='primary'>保存する</Button>
                        </Grid>
                    </Grid>
                </div>
            </Dialog>
            <Snackbar anchorOrigin={{vertical:"top",horizontal:"center"}} open={openSnackState} autoHideDuration={2000} onClose={() => {setOpenSnackState(false)}}>
                <Alert onClose={() => {setOpenSnackState(false)}} severity="success">
                    保存しました
                </Alert>
            </Snackbar>
        </div>
    )
}

export default EditAccountDialog