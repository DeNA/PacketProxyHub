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
import {Button} from "@material-ui/core";
import {Add} from "@material-ui/icons";
import {Account, Org} from "../../../../../Redux";
import CreateOrgMemberDialog from "../../../Dialog/OrgMember/CreateOrgMemberDialog";
import {useDispatch} from "react-redux";
import ApiClient from "../../../../../Common/ApiClient";
import OrgMemberTable from "./OrgMemberTable";

interface Props {
    org: Org
}

const TabOrgMembersView : React.FC<Props> = ({org}) => {

    const [openCreateDlgState, setOpenCreateDlgState] = useState(false)

    const dispatch = useDispatch()
    const apiClient = new ApiClient()

    const handleOnCreate = (org: Org, account: Account, role: string) : void => {
        (async () => {
            apiClient.createOrgMemberAndRedux(dispatch, org.id, account.id, role)
        })()
    }

    return (
        <div style={{width:"90%",margin:"0 auto",marginTop:"2em"}}>
            <div>
                <Button variant="outlined" size="small" color="primary" startIcon={<Add />} onClick={() => {setOpenCreateDlgState(true)}}>
                    メンバーアサイン
                </Button>
            </div>
            <div style={{margin:"1em 0"}}>
                <OrgMemberTable org={org} />
            </div>
            <CreateOrgMemberDialog
                open={openCreateDlgState}
                org={org}
                onCreate={handleOnCreate}
                onClose={() => {setOpenCreateDlgState(false)}}
            />

        </div>
    )
}

export default TabOrgMembersView