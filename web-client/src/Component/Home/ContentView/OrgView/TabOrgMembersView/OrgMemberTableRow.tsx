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
import {IconButton, Link, TableCell, TableRow} from "@material-ui/core";
import {Delete, Edit, Mail, Person} from "@material-ui/icons";
import {OrgMember} from "../../../../../Redux";
import AccountName from "./AccountName";
import AccountMail from "./AccountMail";
import IconText from "../../../../../Common/IconText";
import {blue} from "@material-ui/core/colors";
import {DeleteOrgMemberConfirmDialog} from "../../../Dialog";
import EditOrgMemberDialog from "../../../Dialog/OrgMember/EditOrgMemberDialog";

interface Props {
    orgMember: OrgMember
}

const OrgMemberTableRow : React.FC<Props> = ({orgMember}) => {

    const [openEditDlgState, setOpenEditDlgState] = useState(false)
    const [openDeleteDlgState, setOpenDeleteDlgState] = useState(false)

    return (
        <TableRow key={orgMember.id}>
            <TableCell>
                <IconText
                    icon={<Person color="primary" style={{margin:"0 0.3em 0 0"}}/>}
                    text={<Link color="inherit"><AccountName orgMember={orgMember} /></Link>}
                />
            </TableCell>
            <TableCell>
                <IconText
                    icon={<Mail color="primary" style={{margin:"0 0.3em 0 0"}}/>}
                    text={<Link color="inherit"><AccountMail orgMember={orgMember} /></Link>}
                />
            </TableCell>
            <TableCell>
                {orgMember.role}
            </TableCell>
            <TableCell align="center" style={{width:"5%",padding:"0"}}>
                <IconButton
                    onClick={() => {
                        setOpenEditDlgState(true)
                    }}
                >
                    <Edit style={{color:blue[600]}}/>
                </IconButton>
            </TableCell>
            <TableCell align="center" style={{width:"5%",padding:"0"}}>
                <IconButton
                    onClick={() => {
                        setOpenDeleteDlgState(true)
                    }}
                >
                    <Delete color="secondary"/>
                </IconButton>
            </TableCell>
            <DeleteOrgMemberConfirmDialog
                open={openDeleteDlgState}
                orgMember={orgMember}
                onClose={() => {setOpenDeleteDlgState(false)}}
            />
            <EditOrgMemberDialog
                open={openEditDlgState}
                orgMember={orgMember}
                onClose={() => {setOpenEditDlgState(false)}}
            />
        </TableRow>
    )
}

export default OrgMemberTableRow