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

import React, {useEffect, useState} from 'react'
import {Button, Dialog, DialogTitle, Grid, List, ListItem, TextField} from "@material-ui/core";
import ApiClient from "../../../../Common/ApiClient";
import {useDispatch} from "react-redux";
import {Snapshot} from "../../../../Redux";

export interface OnEditCB {
    (snapshot : Snapshot) : void
}

interface Props {
    open: boolean
    snapshot: Snapshot
    onEditDone?: (snapshot: Snapshot) => void
    onError?: () => void
    onClose: () => void
}

const EditSnapshotDialog : React.FC<Props> = ({ open, snapshot, onEditDone, onError, onClose }) => {

    const [name, setName] = useState("")
    const [description, setDescription] = useState("")

    const dispatch = useDispatch()
    const apiClient = new ApiClient()

    useEffect(() => {
        setName(snapshot.name)
        setDescription(snapshot.description)
    },[open, snapshot])

    const handleOnCancel = () => {
        onClose();
    }

    const handleOnEdit = () => {
        (async () => {
            const newSnapshot = await apiClient.editSnapshotAndRedux(dispatch, {...snapshot, name, description})
            if (newSnapshot) {
                onEditDone && onEditDone(newSnapshot)
            } else {
                onError && onError()
            }
            onClose()
        })()
    }

    const handleOnChangeName = (event: React.ChangeEvent<HTMLInputElement>) => {
        setName(event.currentTarget.value)
    }

    const handleOnChangeDescription = (event: React.ChangeEvent<HTMLInputElement>) => {
        setDescription(event.currentTarget.value)
    }

    return (
        <Dialog onClose={handleOnCancel} open={open} fullWidth={true} maxWidth={'sm'} >
            <DialogTitle>?????????????????????</DialogTitle>
            <List >
                <ListItem>
                    <TextField
                        fullWidth={true}
                        id="name"
                        defaultValue={name}
                        label="???????????????"
                        onChange={handleOnChangeName}
                    />
                </ListItem>
                <ListItem>
                    <TextField
                        fullWidth={true}
                        id="description"
                        defaultValue={description}
                        label="??????"
                        onChange={handleOnChangeDescription}
                    />
                </ListItem>
            </List>
            <div style={{padding:20}}>
                <Grid container justify="center">
                    <Grid item>
                        <Button onClick={handleOnCancel} color='primary'>???????????????</Button>
                    </Grid>
                    <Grid item>
                        <Button variant="contained" onClick={handleOnEdit} color='primary'>????????????</Button>
                    </Grid>
                </Grid>
            </div>
        </Dialog>
    );
}

export default EditSnapshotDialog