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
import {Org} from "../../../../Redux";

interface Props {
    open: boolean
    org: Org
    onEdit: (org: Org) => void
    onClose: () => void
}

const EditOrgDialog : React.FC<Props> = ({open, org, onEdit, onClose}) => {

    const [name, setName] = useState("")
    const [description, setDescription] = useState("")

    useEffect(() => {
        setName(org.name)
        setDescription(org.description)
    },[org])

    const handleCancel = () => {
        setName(org.name)
        setDescription(org.description)
        onClose();
    }

    const handleOnEdit = () => {
        onEdit({...org, name, description})
        onClose();
    }

    const handleOnChangeName = (event: React.ChangeEvent<HTMLInputElement>) => {
        setName(event.currentTarget.value)
    }

    const handleOnChangeDescription = (event: React.ChangeEvent<HTMLInputElement>) => {
        setDescription(event.currentTarget.value)
    }

    return (
        <Dialog onClose={handleCancel} open={open} fullWidth={true} maxWidth={'sm'} >
            <DialogTitle>組織の編集</DialogTitle>
            <List >
                <ListItem>
                    <TextField
                        fullWidth={true}
                        id="name"
                        defaultValue={name}
                        label="組織の名前"
                        onChange={handleOnChangeName}
                    />
                </ListItem>
                <ListItem>
                    <TextField
                        fullWidth={true}
                        id="description"
                        defaultValue={description}
                        label="組織の簡単な説明"
                        onChange={handleOnChangeDescription}
                    />
                </ListItem>
            </List>
            <div style={{padding:20}}>
                <Grid container justify="center">
                    <Grid item>
                        <Button onClick={handleCancel} color='primary'>キャンセル</Button>
                    </Grid>
                    <Grid item>
                        <Button variant="contained" onClick={handleOnEdit} color='primary'>作成する</Button>
                    </Grid>
                </Grid>
            </div>
        </Dialog>
    );
}

export default EditOrgDialog