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

import React, {useEffect, useState} from 'react'
import {Button, Dialog, DialogTitle, Grid, List, ListItem, TextField} from "@material-ui/core"
import {CloudUpload} from "@material-ui/icons"
import Alert from "@material-ui/lab/Alert"
import {Send, UploadFile} from "@mui/icons-material"
import {LoadingButton} from "@mui/lab"
import {Binary, Config} from "../../../../Redux"
import ApiClient from "../../../../Common/ApiClient"
import {useDispatch} from "react-redux"
import Dropzone from "react-dropzone"
import IconText from "../../../../Common/IconText";

interface Props {
    open: boolean
    config: Config
    onCreateDone?: (binary:Binary) => void
    onClose: () => void
}

const CreateBinaryDialog : React.FC<Props> = ({ open, config, onCreateDone, onClose }) => {

    const [name, setName] = useState("")
    const [description, setDescription] = useState("")
    const [uploadFile, setUploadFile] = useState<File|undefined>(undefined)
    const [loading, setLoading] = useState(false)
    const [loadingText, setLoadingText] = useState("アップロード")

    const dispatch = useDispatch()
    const apiClient = new ApiClient()

    useEffect(() => {
        if (open) {
            setName("")
            setDescription("")
            setUploadFile(undefined)
            setLoading(false)
            setLoadingText("アップロード")
        }
    },[open])

    const handleOnCancel = () => {
        onClose();
    }

    const handleOnChangeDescription = (event: React.ChangeEvent<HTMLInputElement>) => {
        setDescription(event.currentTarget.value)
    }

    const handleOnDrop = (acceptedFiles:File[]) => {
        const file:File = acceptedFiles!.pop()!
        setUploadFile(file)
        setName(file.name)
    }

    const handleOnClickUploadButton = () => {
        setLoading(true)
        if (!uploadFile) {
            setLoading(false)
            return
        }
        (async () => {
            const uploadId = await apiClient.multiUploadFile(config.orgId, uploadFile, (progress) => {
                setLoadingText(Math.ceil(progress).toString(10) + "%")
            }, () => {
                setLoadingText("Finalizing...")
            })
            const binary: Binary | undefined = await apiClient.createBinaryAndRedux(dispatch, config.orgId, config.projectId, config.id, name, description, uploadId)
            if (binary !== undefined) {
                onCreateDone && onCreateDone(binary)
            }
            setLoading(false)
            onClose()
        })()
    }

    return (
        <Dialog onClose={handleOnCancel} open={open} fullWidth={true} maxWidth={'sm'} >
            <DialogTitle>APK/IPAのアップロード</DialogTitle>
            <List >
                <ListItem>
                    { !uploadFile && (
                    <Dropzone onDrop={handleOnDrop}>
                        {
                            ({getRootProps, getInputProps}) => (
                                <div style={{width:"100%"}} {...getRootProps()}>
                                    <input {...getInputProps()} />
                                    <Alert icon={false} severity="error">
                                        <div style={{display:"flex",justifyContent:"center",alignItems:"center",height:"50px"}} >
                                            <IconText icon={<CloudUpload />} text={"ファイルをここにドロップするか、クリックしてファイルを選択してください"} />
                                        </div>
                                    </Alert>
                                </div>
                            )
                        }
                    </Dropzone>
                    )}
                </ListItem>
                { uploadFile && (
                    <ListItem>
                        <div style={{width:"100%"}} >
                            <Alert icon={false} severity="info">
                                <IconText icon={<UploadFile />} text={uploadFile.name} />
                            </Alert>
                        </div>
                    </ListItem>
                )}
                <ListItem>
                    <TextField
                        fullWidth={true}
                        id="description"
                        defaultValue={description}
                        label="ファイルの概要"
                        onChange={handleOnChangeDescription}
                    />
                </ListItem>
            </List>
            <div style={{padding:20}}>
                <Grid container justify="center">
                    <Grid item>
                        <Button onClick={handleOnCancel} color='primary'>キャンセル</Button>
                    </Grid>
                    <Grid item>
                        <LoadingButton
                            variant="contained"
                            disabled={!uploadFile}
                            loading={loading}
                            loadingPosition="start"
                            startIcon={<Send />}
                            onClick={handleOnClickUploadButton}
                            color='primary'
                        >
                            { loadingText }
                        </LoadingButton>
                    </Grid>
                </Grid>
            </div>
        </Dialog>
    );
}

export default CreateBinaryDialog