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

import React, {useState} from 'react'
import {Button, Dialog, DialogTitle, Grid, List, ListItem, TextField} from "@material-ui/core";

export type CreateOrgArg = {
    name: string;
    description: string;
}

export type CreateOrgDialogProps = {
    open: boolean;
    onCreate: (args: CreateOrgArg) => void;
    onClose: () => void;
}

const CreateOrgDialog = (props: CreateOrgDialogProps) => {
    const { onClose, onCreate, open } = props;
    const [ name, setName ] = useState("");
    const [ description, setDescription ] = useState("");

    const handleClose = () => {
        onClose();
    };

    const handleOnCreate = () => {
        onCreate({name, description});
        onClose();
    };

    const handleOnChangeName = (event: React.ChangeEvent<HTMLInputElement>) => {
        setName(event.currentTarget.value);
    }

    const handleOnChangeDescription = (event: React.ChangeEvent<HTMLInputElement>) => {
        setDescription(event.currentTarget.value);
    }

    return (
        <Dialog onClose={handleClose} open={open} fullWidth={true} maxWidth={'sm'}>
            <DialogTitle>組織の新規作成</DialogTitle>
            <List >
                <ListItem>
                    <TextField fullWidth={true} id="name" label="組織の名前" onChange={handleOnChangeName}/>
                </ListItem>
                <ListItem>
                    <TextField fullWidth={true} id="description" label="組織の簡単な説明" onChange={handleOnChangeDescription}/>
                </ListItem>
            </List>
            <div style={{padding:20}}>
                <Grid container justify="center">
                    <Grid item>
                        <Button onClick={handleClose} color='primary'>キャンセル</Button>
                    </Grid>
                    <Grid item>
                        <Button variant="contained" onClick={handleOnCreate} color='primary'>作成する</Button>
                    </Grid>
                </Grid>
            </div>
        </Dialog>
    );
}

export default CreateOrgDialog