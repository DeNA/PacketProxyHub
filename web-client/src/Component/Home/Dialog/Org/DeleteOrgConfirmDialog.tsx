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

import React from 'react'
import {Button, Dialog, DialogContent, DialogContentText, DialogTitle, Grid} from "@material-ui/core";
import {Org} from "../../../../Redux";

interface Props {
    open: boolean
    org: Org
    onDelete: (org: Org) => void
    onClose: () => void
}

const DeleteOrgConfirmDialog : React.FC<Props> = ({ open, org, onDelete, onClose }) => {

    const handleClose = () => {
        onClose();
    }

    const handleOnDelete = () => {
        onDelete(org)
        onClose()
    }

    return (
        <Dialog onClose={handleClose} open={open} fullWidth={true} maxWidth={'sm'}>
            <DialogTitle>組織の削除の確認</DialogTitle>
            <DialogContent>
                <DialogContentText>
                    <span>組織： <strong>{org.name}</strong> を本当に削除しても良いですか？</span>
                </DialogContentText>
            </DialogContent>
            <div style={{padding:20}}>
                <Grid container justify="center">
                    <Grid item>
                        <Button style={{margin:"0 1em"}} onClick={handleClose} color='primary'>キャンセル</Button>
                    </Grid>
                    <Grid item>
                        <Button style={{margin:"0 1em"}} variant="contained" onClick={handleOnDelete} color='primary'>削除</Button>
                    </Grid>
                </Grid>
            </div>
        </Dialog>
    );
}

export default DeleteOrgConfirmDialog