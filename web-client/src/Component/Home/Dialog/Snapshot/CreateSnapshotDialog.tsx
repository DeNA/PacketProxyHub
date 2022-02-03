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
import {Config, Snapshot} from "../../../../Redux"
import ApiClient from "../../../../Common/ApiClient"
import {useDispatch} from "react-redux"
import Dropzone from "react-dropzone"
import IconText from "../../../../Common/IconText";
import Resizer from "react-image-file-resizer"

interface Props {
    open: boolean
    config: Config
    onCreateDone?: (snapshot:Snapshot) => void
    onClose: () => void
}

const CreateSnapshotDialog : React.FC<Props> = ({ open, config, onCreateDone, onClose }) => {

    const [name, setName] = useState("")
    const [description, setDescription] = useState("")
    const [uploadFile, setUploadFile] = useState<File|undefined>(undefined)
    const [loading, setLoading] = useState(false)
    const [loadingText, setLoadingText] = useState("アップロード")
    const [preview, setPreview] = useState('')

    const dispatch = useDispatch()
    const apiClient = new ApiClient()

    useEffect(() => {
        if (open) {
            setName("")
            setDescription("")
            setUploadFile(undefined)
            setLoading(false)
            setLoadingText("アップロード")
            setPreview('')
        }
    },[open])

    const handleOnCancel = () => {
        onClose();
    }

    const handleOnChangeDescription = (event: React.ChangeEvent<HTMLInputElement>) => {
        setDescription(event.currentTarget.value)
    }

    const handleOnDrop = (acceptedFiles:File[]) => {
        (async () => {
            const file:File = acceptedFiles!.pop()!
            if (!await checkFile(file)) {
                alert(`incorrect PacketProxy snapshot file: ${file.name}`)
                return
            }
            setUploadFile(file)
            setName(file.name)
            setPreview(await resizeFile(await getSnapshotImage(file)))
        })()
    }

    const handleOnClickUploadButton = () => {
        setLoading(true)
        if (!uploadFile) {
            setLoading(false)
            return
        }
        (async () => {
            const androidVersion = await getSnapshotAndroidVersion(uploadFile)
            const googlePlay = await getSnapshotGooglePlay(uploadFile)
            const snapshotId = await apiClient.uploadFile(config.orgId, await getSnapshotImage(uploadFile))
            const uploadId = await apiClient.multiUploadFile(config.orgId, uploadFile, (progress) => {
                setLoadingText(Math.ceil(progress).toString(10) + "%")
            }, () => {
                setLoadingText("Finalizing...")
            })
            const snapshot: Snapshot | undefined = await apiClient.createSnapshotAndRedux(dispatch, config.orgId, config.projectId, config.id, name, description, androidVersion, googlePlay, uploadId, snapshotId)
            if (snapshot !== undefined) {
                onCreateDone && onCreateDone(snapshot)
            }
            setLoading(false)
            onClose()
        })()
    }

    const resizeFile = (file: Blob): Promise<string> => {
        return new Promise((resolve) => {
            Resizer.imageFileResizer(
                file,
                300,
                300,
                'PNG',
                100,
                0,
                (uri) => {
                    resolve(uri as string)
                },
                'base64'
            )
        })
    }

    const checkFile = async (file:File): Promise<Boolean> => {
        const magicWord = await file.slice(0, 4).text()
        return magicWord === "PPHS"
    }

    const getSnapshotGooglePlay = async(file:File) : Promise<number> => {
        return new Int8Array(await file.slice(4, 4 + 1).arrayBuffer())[0]
    }

    const getSnapshotAndroidVersion = async(file:File) : Promise<string> => {
        return file.slice(4 + 1, 4 + 1 + 64).text()
    }

    const getSnapshotImage = async(file:File) : Promise<Blob> => {
        const screenshotSize = new Int32Array(await file.slice(4 + 1 + 64, 4 + 1 + 64 + 4).arrayBuffer())[0]
        return file.slice(4 + 1 + 64 + 4, 4 + 1 + 64 + 4 + screenshotSize)
    }

    return ( <Dialog onClose={handleOnCancel} open={open} fullWidth={true} maxWidth={'sm'} > <DialogTitle>スナップショットのアップロード</DialogTitle>
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
                        <div style={{width:"100%",backgroundColor:"rgb(232, 244, 253)"}} >
                            <div style={{padding:"0.5em 0.5em 0.5em 0.5em"}}>
                                <IconText icon={<UploadFile />} text={uploadFile.name} />
                            </div>
                            <div style={{padding:"0.5em 0 0.5em 0",display:"flex",flexDirection:"column",alignItems:"center",justifyContent:"center"}}>
                                <img src={preview} />
                            </div>
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

export default CreateSnapshotDialog