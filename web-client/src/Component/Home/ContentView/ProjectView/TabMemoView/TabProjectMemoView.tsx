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
import {Button, Snackbar} from "@material-ui/core";
import {Project, projectsUpdate} from "../../../../../Redux";
import {useDispatch} from "react-redux";
import SimpleMDEEditor from "react-simplemde-editor";
import 'easymde/dist/easymde.min.css';
import {Alert} from "@material-ui/lab";
import ApiClient from "../../../../../Common/ApiClient";
import EasyMDE from "easymde"

interface Props {
    project: Project
}

const TabProjectMemoView : React.FC<Props> = ({project}) => {

    const template = `# ${project.name}
${project.description}`

    const dispatch = useDispatch()
    const [openState, setOpenState] = useState(false)

    const handleOnChange = (value: string) => {
        dispatch(projectsUpdate({id: project.id, changes: {...project, content:value}}))
    }

    const handleOnSave = () => {
        (async () => {
            const apiClient = new ApiClient()
            await apiClient.editProjectAndRedux(dispatch, project)
            setOpenState(true)
        })()
    }

    return (
        <div style={{width:"90%",margin:"0 auto",marginTop:"2em"}}>
            <div>
                <Button variant="contained" size="small" color="primary" onClick={handleOnSave}>
                    保存
                </Button>
            </div>
            <div style={{margin:"1em 0"}}>
                <SimpleMDEEditor
                    getMdeInstance={(instance) => EasyMDE.togglePreview(instance) }
                    onChange={handleOnChange}
                    value={project.content.length > 0 ? project.content : template}
                    options={{minHeight:"500px",maxHeight:"auto"}}
                />
            </div>
            <Snackbar anchorOrigin={{vertical:"top",horizontal:"center"}} open={openState} autoHideDuration={2000} onClose={() => {setOpenState(false)}}>
                <Alert onClose={() => {setOpenState(false)}} severity="success">
                    保存しました
                </Alert>
            </Snackbar>
        </div>
    )
}

export default TabProjectMemoView