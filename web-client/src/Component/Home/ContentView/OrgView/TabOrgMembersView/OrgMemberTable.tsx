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

import React, {useEffect} from "react";
import {Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow} from "@material-ui/core";
import {Org, OrgMember, orgMembersSelectors} from "../../../../../Redux";
import {useDispatch, useSelector} from "react-redux";
import OrgMemberTableRow from "./OrgMemberTableRow";
import ApiClient from "../../../../../Common/ApiClient";
import {useHistory} from "react-router-dom";

interface Props {
    org: Org
}

const OrgMemberTable : React.FC<Props> = ({org}) => {

    const orgMembers : OrgMember[] = useSelector(orgMembersSelectors.selectAll).filter((om) => {
        return om.orgId === org.id
    })

    const dispatch = useDispatch()
    const apiClient = new ApiClient()
    const history = useHistory()

    useEffect(() => {
        (async() => {
            const orgMembers = await apiClient.fetchOrgMembersAndRedux(dispatch, org.id)
            if (orgMembers === undefined) {
                history.push("/")
            }
        })()
    }, [org.id])

    return (
        <TableContainer component={Paper} style={{backgroundColor:"#FFFFFF"}}>
            <Table aria-label="simple table">
                <TableHead>
                    <TableRow>
                        <TableCell style={{fontWeight:"bold"}}>メンバー名</TableCell>
                        <TableCell style={{fontWeight:"bold"}}>メール</TableCell>
                        <TableCell style={{fontWeight:"bold"}}>ロール</TableCell>
                        <TableCell align="center" style={{fontWeight:"bold",width:"5%",padding:"0"}}>編集</TableCell>
                        <TableCell align="center" style={{fontWeight:"bold",width:"5%",padding:"0"}}>削除</TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {
                        orgMembers.map((orgMember) => {
                            return <OrgMemberTableRow key={orgMember.id} orgMember={orgMember} />
                        })
                    }
                </TableBody>
            </Table>
        </TableContainer>
    )
}

export default OrgMemberTable