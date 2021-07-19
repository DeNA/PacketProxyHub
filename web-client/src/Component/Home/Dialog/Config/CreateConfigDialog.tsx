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
import {Config, Project} from "../../../../Redux";
import ApiClient from "../../../../Common/ApiClient";
import {useDispatch} from "react-redux";

interface Props {
    open: boolean
    project: Project
    onCreateDone?: (config:Config) => void
    onClose: () => void
}

const CreateConfigDialog : React.FC<Props> = ({ open, project, onCreateDone, onClose }) => {

    const [name, setName] = useState("")
    const [description, setDescription] = useState("")

    const dispatch = useDispatch()
    const apiClient = new ApiClient()

    useEffect(() => {
        setName("")
        setDescription("")
    },[open])

    const handleOnCancel = () => {
        onClose();
    }

    const handleOnCreate = () => {
        (async () => {
            const config = await apiClient.createConfigAndRedux(dispatch, project.orgId, project.id, name, description)
            if (config !== undefined) {
                onCreateDone && onCreateDone(config)
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
            <DialogTitle>コンフィグの作成</DialogTitle>
            <List >
                <ListItem>
                    <TextField
                        fullWidth={true}
                        id="name"
                        defaultValue={name}
                        label="新規コンフィグ名"
                        onChange={handleOnChangeName}
                    />
                </ListItem>
                <ListItem>
                    <TextField
                        fullWidth={true}
                        id="description"
                        defaultValue={description}
                        label="概要"
                        onChange={handleOnChangeDescription}
                    />
                </ListItem>
            </List>
            <div style={{padding:20}}>
                <Grid container justify="center">
                    <Grid item>
                        <Button onClick={handleOnCancel} color='primary'>キャンセル</Button>
                    </Grid>
                    <Grid item>
                        <Button variant="contained" onClick={handleOnCreate} color='primary'>作成する</Button>
                    </Grid>
                </Grid>
            </div>
        </Dialog>
    );
}

export default CreateConfigDialog