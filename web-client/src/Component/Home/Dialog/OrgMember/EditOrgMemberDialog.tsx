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
import {
    Button,
    Dialog,
    DialogTitle,
    FormControl,
    Grid,
    InputLabel,
    List,
    ListItem,
    MenuItem,
    Select,
    Snackbar,
    TextField
} from "@material-ui/core";
import {accountsSelectors, OrgMember} from "../../../../Redux";
import ApiClient from "../../../../Common/ApiClient";
import {useDispatch, useSelector} from "react-redux";
import {Alert} from "@material-ui/lab";

interface Props {
    open: boolean
    orgMember: OrgMember
    onEditDone?: (orgMember: OrgMember) => void
    onClose: () => void
}

const EditOrgMemberDialog : React.FC<Props> = ({ open, orgMember, onEditDone, onClose }) => {

    const [role, setRole] = useState(orgMember.role)
    const [ openErrorState, setOpenErrorState ] = useState(false)

    const dispatch = useDispatch()
    const apiClient = new ApiClient()
    const account = useSelector(accountsSelectors.selectAll).find(a => a.id === orgMember.accountId)

    const handleOnCancel = () => {
        onClose();
    }

    const handleOnEdit = () => {
        (async () => {
            const newOrgMember = await apiClient.editOrgMemberAndRedux(dispatch, {...orgMember, role})
            if (newOrgMember) {
                onEditDone && onEditDone(newOrgMember)
                onClose()
            } else {
                setOpenErrorState(true)
            }
        })()
    }

    const handleOnChangeRole = (event: React.ChangeEvent<{ value: unknown }>) => {
        setRole(event.target.value as string);
    }

    return (
        <Dialog onClose={handleOnCancel} open={open} fullWidth={true} maxWidth={'sm'} >
            <DialogTitle>組織メンバーの編集</DialogTitle>
            <List >
                <ListItem>
                    <div style={{width:"100%",display:"flex",alignItems:"center",justifyContent:"center"}} >
                        <TextField fullWidth={true} disabled={true} label="アカウント名" value={
                            account && account.name
                        }>
                        </TextField>
                    </div>
                </ListItem>
                <ListItem>
                    <FormControl variant="outlined" >
                        <InputLabel id="role-label">ロール</InputLabel>
                        <Select
                            labelId="role-label"
                            id="role-id"
                            value={role}
                            onChange={handleOnChangeRole}
                            label="ロール"
                        >
                            <MenuItem value={"Owner"}>Owner</MenuItem>
                            <MenuItem value={"Member"}>Member</MenuItem>
                        </Select>
                    </FormControl>
                </ListItem>
            </List>
            <div style={{padding:20}}>
                <Grid container justify="center">
                    <Grid item>
                        <Button onClick={handleOnCancel} color='primary'>キャンセル</Button>
                    </Grid>
                    <Grid item>
                        <Button variant="contained" onClick={handleOnEdit} color='primary'>保存する</Button>
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

export default EditOrgMemberDialog