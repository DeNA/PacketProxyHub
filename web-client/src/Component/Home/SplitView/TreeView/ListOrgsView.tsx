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

import React, {useState} from "react"
import {Config, Org, orgsSelectors, Project} from "../../../../Redux";
import {Button, List} from "@material-ui/core";
import {Add} from "@material-ui/icons";
import ListOrgView from "./ListOrgView";
import {green} from "@material-ui/core/colors";
import ApiClient from "../../../../Common/ApiClient";
import {useDispatch, useSelector} from "react-redux";
import CreateOrgDialog, {CreateOrgArg} from "../../Dialog/Org/CreateOrgDialog";
import IconText from "../../../../Common/IconText";

interface Props {
    selectedId: string
    onClickOrg: (org: Org) => void
    onClickProject: (org: Org, project: Project) => void
    onClickConfig: (org: Org, project: Project, config: Config) => void
}

const ListOrgsView : React.FC<Props> = ({ selectedId, onClickOrg, onClickProject, onClickConfig }) => {

    const dispatch = useDispatch()
    const apiClient = new ApiClient()
    const orgs = useSelector(orgsSelectors.selectAll)

    const [openCreateOrgDlgState, setOpenCreateDlgState] = useState(false)

    const handleOnCreateOrg = (args: CreateOrgArg) : void => {
        (async () => {
            await apiClient.createOrgAndRedux(dispatch, args.name, args.description)
        })()
    }

    return (
        <div>
            <Button size="small" variant="outlined" style={{color:green[500],margin:"5px",borderColor:green[500]}} onClick={() => setOpenCreateDlgState(true)}>
                <IconText icon={<Add />} text="新規組織" />
            </Button>
            <List component="div" dense={false} disablePadding>
                {
                    orgs.map((org, idx) => {
                        return <ListOrgView
                            key={org.id}
                            selectedId={selectedId}
                            org={org}
                            onClickOrg={onClickOrg}
                            onClickProject={onClickProject}
                            onClickConfig={onClickConfig}
                        />
                    })
                }
            </List>
            <CreateOrgDialog
                open={openCreateOrgDlgState}
                onCreate={handleOnCreateOrg}
                onClose={() => setOpenCreateDlgState(false)}
            />
        </div>
    )
}

export default ListOrgsView
