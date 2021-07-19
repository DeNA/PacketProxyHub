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
    Snackbar
} from "@material-ui/core";
import {Account, accountsSelectors, Org, orgMembersSelectors} from "../../../../Redux";
import {useDispatch, useSelector} from "react-redux";
import SearchableList from "../../../../Common/SearchableList";
import ApiClient from "../../../../Common/ApiClient";
import {Alert} from "@material-ui/lab";

interface Props {
    open: boolean
    org: Org
    onCreate: ( org: Org, account: Account, role: string) => void
    onClose: () => void
}

const CreateOrgMemberDialog : React.FC<Props> = ({ open, org, onCreate, onClose }) => {

    const [ name, setName ] = useState("")
    const [ role, setRole ] = useState("Owner")
    const [ matched, setMatched ] = useState(false)
    const [ openErrorState, setOpenErrorState ] = useState(false)

    const dispatch = useDispatch()
    const members = useSelector(orgMembersSelectors.selectAll).filter(om => om.orgId === org.id).map(om => om.accountId)
    const accountCands = useSelector(accountsSelectors.selectAll).filter(a => !members.includes(a.id)).map(a => a.name)
    const account = useSelector(accountsSelectors.selectAll).find(a => a.name === name)
    const apiClient = new ApiClient()

    const handleOnMatched = (value: string) => {
        setName(value)
        setMatched(true)
    }

    const handleOnUnmatched = (value: string) => {
        setMatched(false)
    }

    const handleClose = () => {
        onClose();
    }

    const handleOnChangeRole = (event: React.ChangeEvent<{ value: unknown }>) => {
        setRole(event.target.value as string);
    }

    const handleOnCreate = () => {
        (async() => {
            if (account) {
                const orgMember = await apiClient.createOrgMemberAndRedux(dispatch, org.id, account.id, role)
                if (orgMember === undefined) {
                    setOpenErrorState(true)
                } else {
                    onClose()
                }
            }
        })()
    }

    return (
        <Dialog onClose={handleClose} open={open} fullWidth={true} maxWidth={'sm'} >
            <DialogTitle>組織メンバーの追加</DialogTitle>
            <List>
                <ListItem>
                    <div style={{width:"100%",display:"flex",alignItems:"center",justifyContent:"center"}} >
                        <SearchableList
                            label="追加メンバー名"
                            items={accountCands}
                            value=""
                            onMatched={handleOnMatched}
                            onUnmatched={handleOnUnmatched}
                        />
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
                        <Button onClick={handleClose} color='primary'>キャンセル</Button>
                    </Grid>
                    <Grid item>
                        <Button
                            variant="contained"
                            onClick={handleOnCreate}
                            color='primary'
                            disabled={!matched}
                        >
                            追加する
                        </Button>
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

export default CreateOrgMemberDialog