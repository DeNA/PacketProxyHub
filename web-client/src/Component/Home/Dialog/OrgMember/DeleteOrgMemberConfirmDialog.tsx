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
import {Button, Dialog, DialogContent, DialogContentText, DialogTitle, Grid, Snackbar} from "@material-ui/core";
import {accountsSelectors, OrgMember, orgsSelectors} from "../../../../Redux";
import {useDispatch, useSelector} from "react-redux";
import {RootState} from "../../../../Redux/store";
import ApiClient from "../../../../Common/ApiClient";
import {Alert} from "@material-ui/lab";

interface Props {
    open: boolean
    orgMember: OrgMember
    onDeleteDone?: (orgMember: OrgMember) => void
    onClose: () => void
}

const DeleteOrgMemberConfirmDialog : React.FC<Props> = ({ open, orgMember, onDeleteDone, onClose }) => {

    const dispatch = useDispatch()
    const apiClient = new ApiClient()

    const [ openErrorState, setOpenErrorState ] = useState(false)

    const account = useSelector((state: RootState) => {
        return accountsSelectors.selectById(state, orgMember.accountId);
    });

    const org = useSelector((state: RootState) => {
        return orgsSelectors.selectById(state, orgMember.orgId);
    });

    const handleClose = () => {
        onClose();
    }

    const handleOnDelete = (orgMember: OrgMember) => {
        (async () => {
            const ret = await apiClient.deleteOrgMemberAndRedux(dispatch, orgMember)
            if (ret === undefined) {
                setOpenErrorState(true)
            } else {
                onDeleteDone && onDeleteDone(orgMember)
                onClose()
            }
        })()
    }

    return (
        <Dialog onClose={handleClose} open={open} fullWidth={true} maxWidth={'sm'}>
            <DialogTitle>組織メンバーの削除の確認</DialogTitle>
            <DialogContent>
                <DialogContentText>
                    <span>
                        メンバー <strong>{account && account.name}</strong> を組織 <strong>{org && org.name}</strong> から削除しても良いですか？
                    </span>
                </DialogContentText>
            </DialogContent>
            <div style={{padding:20}}>
                <Grid container justify="center">
                    <Grid item>
                        <Button style={{margin:"0 1em"}} onClick={handleClose} color='primary'>キャンセル</Button>
                    </Grid>
                    <Grid item>
                        <Button style={{margin:"0 1em"}} variant="contained" onClick={() => {handleOnDelete(orgMember)}} color='primary'>削除</Button>
                    </Grid>
                </Grid>
            </div>
            <Snackbar anchorOrigin={{vertical:"top",horizontal:"center"}}
                      open={openErrorState}
                      autoHideDuration={2000}
                      onClose={() => {setOpenErrorState(false)}}
            >
                <Alert onClose={() => {setOpenErrorState(false)}} severity="error">
                    権限がありません
                </Alert>
            </Snackbar>
        </Dialog>
    );
}

export default DeleteOrgMemberConfirmDialog