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

import React, {useEffect, useState} from "react";
import {
    Button,
    IconButton,
    Link,
    Paper,
    Snackbar,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow
} from "@material-ui/core";
import {Config, initialSnapshot, Snapshot, snapshotsSelectors} from "../../../../../Redux";
import {useDispatch, useSelector} from "react-redux";
import 'easymde/dist/easymde.min.css';
import {Alert} from "@material-ui/lab";
import ApiClient from "../../../../../Common/ApiClient";
import CreateSnapshotDialog from "../../../Dialog/Snapshot/CreateSnapshotDialog";
import {Add, Android, Delete, Edit} from "@material-ui/icons";
import IconText from "../../../../../Common/IconText";
import {blue, green} from "@material-ui/core/colors";
import AccountName from "../../../../../Common/AccountName";
import PHDate from "../../../../../Common/PHDate";
import {useHistory} from "react-router-dom";
import DeleteSnapshotConfirmDialog from "../../../Dialog/Snapshot/DeleteSnapshotConfirmDialog";
import {Apple, DeviceUnknown, Download} from "@mui/icons-material";
import {red} from "@mui/material/colors";
import EditSnapshotDialog from "../../../Dialog/Snapshot/EditSnapshotDialog";
import Preview from "../../../../../Common/Preview"
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome"
import { faGooglePlay } from "@fortawesome/free-brands-svg-icons"

interface Props {
    config: Config
}

const TabSnapshotView : React.FC<Props> = ({config}) => {

    const apiClient = new ApiClient()
    const dispatch = useDispatch()
    const [openState, setOpenState] = useState(false)
    const [openDlgState, setOpenDlgState] = useState(false)

    const [deleteSnapshot, setDeleteSnapshot] = useState<Snapshot>(initialSnapshot)
    const [openDeleteDlgState, setOpenDeleteDlgState] = useState(false)
    const [editSnapshot, setEditSnapshot] = useState<Snapshot>(initialSnapshot)
    const [openEditDlgState, setOpenEditDlgState] = useState(false)

    const history = useHistory()

    const handleOnDownload = (snapshot:Snapshot) => {
        (async () => {
            const apiClient = new ApiClient()
            apiClient.downloadFile(config.orgId, snapshot.fileId, snapshot.name)
        })()
    }

    const snapshots : Snapshot[] = useSelector(snapshotsSelectors.selectAll).filter((snapshot: Snapshot) => {
        return snapshot.configId === config.id
    })

    useEffect(() => {
        (async () => {
            const snapshots = await apiClient.fetchSnapshotsAndRedux(dispatch, config)
            if (snapshots === undefined) {
                history.push(`/orgs/${config.orgId}/projects/${config.projectId}/configs/${config.id}`)
            }
        })()
    }, [openEditDlgState])

    return (
        <div style={{width:"90%",margin:"0 auto",marginTop:"2em"}}>
            <div>
                <Button variant="outlined" size="small" color="primary" startIcon={<Add />} onClick={() => setOpenDlgState(true)} >
                    新規アップロード
                </Button>
            </div>
            <div style={{margin:"1em 0"}}>
                <TableContainer component={Paper} style={{backgroundColor:"#FFFFFF"}} >
                    <Table aria-label="simple table">
                        <TableHead>
                            <TableRow>
                                <TableCell style={{fontWeight:"bold"}} align="center" width="100px">Screenshot</TableCell>
                                <TableCell style={{fontWeight:"bold"}}>ファイル名</TableCell>
                                <TableCell style={{fontWeight:"bold"}} width="auto">概要</TableCell>
                                <TableCell style={{fontWeight:"bold"}} align="center">Android Version</TableCell>
                                <TableCell style={{fontWeight:"bold"}} align="center">GooglePlay有無</TableCell>
                                <TableCell style={{fontWeight:"bold"}} align="center">アップロード者</TableCell>
                                <TableCell style={{fontWeight:"bold"}} align="center">アップロード時刻</TableCell>
                                <TableCell style={{fontWeight:"bold"}} align="center" width="20px">Download</TableCell>
                                <TableCell style={{fontWeight:"bold"}} align="center" width="20px">編集</TableCell>
                                <TableCell style={{fontWeight:"bold"}} align="center" width="20px">削除</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            { snapshots.sort((a:Snapshot, b:Snapshot) => { return a!.uploadedAt! - b!.uploadedAt! }).map((snapshot) => {
                                return <TableRow key={snapshot.id}>
                                    <TableCell align="center">
                                        { snapshot.resizedScreenshot &&
                                            <Preview resizedFile={snapshot.resizedScreenshot} />
                                        }
                                    </TableCell>
                                    <TableCell>
                                        <Link href="#" color="inherit" onClick={() => handleOnDownload(snapshot)} >
                                            {snapshot.name}
                                        </Link>
                                    </TableCell>
                                    <TableCell width="auto">
                                        { snapshot.description }
                                    </TableCell>
                                    <TableCell align="center">
                                        { snapshot.androidVersion }
                                    </TableCell>
                                    <TableCell align="center">
                                        { snapshot.googlePlay === 1 && <FontAwesomeIcon icon={faGooglePlay} />}
                                    </TableCell>
                                    <TableCell align="center">
                                        <AccountName accountId={snapshot.uploadedBy} />
                                    </TableCell>
                                    <TableCell align="center">
                                        {
                                            new PHDate(snapshot.uploadedAt && snapshot.uploadedAt).toString()
                                        }
                                    </TableCell>
                                    <TableCell align="center" width="20px">
                                        <IconButton onClick={() => handleOnDownload(snapshot)} >
                                            <Download style={{color:green[600]}}/>
                                        </IconButton>
                                    </TableCell>
                                    <TableCell align="center" width="20px">
                                        <IconButton
                                            onClick={() => {
                                                setEditSnapshot(snapshot)
                                                setOpenEditDlgState(true)
                                            }}
                                        >
                                            <Edit style={{color:blue[600]}}/>
                                        </IconButton>
                                    </TableCell>
                                    <TableCell align="center" width="20px">
                                        <IconButton
                                            onClick={() => {
                                                setDeleteSnapshot(snapshot)
                                                setOpenDeleteDlgState(true)
                                            }}
                                        >
                                            <Delete color="secondary"/>
                                        </IconButton>
                                    </TableCell>
                                </TableRow>
                            })}
                        </TableBody>
                    </Table>
                </TableContainer>
            </div>
            <Snackbar anchorOrigin={{vertical:"top",horizontal:"center"}} open={openState} autoHideDuration={2000} onClose={() => {setOpenState(false)}}>
                <Alert onClose={() => {setOpenState(false)}} severity="success">
                    保存しました
                </Alert>
            </Snackbar>
            <CreateSnapshotDialog
                config={config}
                open={openDlgState}
                onClose={() => {setOpenDlgState(false)}}
            />
            <EditSnapshotDialog
                open={openEditDlgState}
                snapshot={editSnapshot}
                onError={() => {
                    history.push(`/orgs/${config.orgId}/projects/${config.projectId}/configs/${config.id}`)
                }}
                onClose={() => setOpenEditDlgState(false)}
            />
            <DeleteSnapshotConfirmDialog
                open={openDeleteDlgState}
                snapshot={deleteSnapshot}
                onError={() => {
                    history.push(`/orgs/${config.orgId}/projects/${config.projectId}/configs/${config.id}`)
                }}
                onClose={() => setOpenDeleteDlgState(false)}
            />
        </div>
    )
}

export default TabSnapshotView