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
import {Org} from "../../../../../Redux";
import {Add} from "@material-ui/icons";
import CreateProjectDialog from "../../../Dialog/Project/CreateProjectDialog";
import ProjectTable from "./ProjectTable";

interface Props {
    org: Org
}

const TabProjectsView : React.FC<Props> = ({org}) => {

    const [openDlgState, setOpenDlgState] = useState(false)

    return (
        <div style={{width:"90%",margin:"0 auto",marginTop:"2em"}}>
            <div>
                <Button variant="outlined" size="small" color="primary" startIcon={<Add />} onClick={() => setOpenDlgState(true)} >
                    新規プロジェクト
                </Button>
            </div>
            <div style={{margin:"1em 0"}}>
                <ProjectTable org={org} />
            </div>
            <CreateProjectDialog
                org={org}
                open={openDlgState}
                onClose={() => {setOpenDlgState(false)}}
            />
        </div>
    )
}

export default TabProjectsView