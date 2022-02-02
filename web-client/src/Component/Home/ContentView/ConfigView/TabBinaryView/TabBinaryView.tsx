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
import {binariesSelectors, Binary, Config, initialBinary, Snapshot, snapshotsSelectors} from "../../../../../Redux";
import {useDispatch, useSelector} from "react-redux";
import 'easymde/dist/easymde.min.css';
import {Alert} from "@material-ui/lab";
import ApiClient from "../../../../../Common/ApiClient";
import CreateBinaryDialog from "../../../Dialog/Binary/CreateBinaryDialog";
import {Add, Android, Delete, Edit} from "@material-ui/icons";
import IconText from "../../../../../Common/IconText";
import {blue, green} from "@material-ui/core/colors";
import AccountName from "../../../../../Common/AccountName";
import PHDate from "../../../../../Common/PHDate";
import {useHistory} from "react-router-dom";
import DeleteBinaryConfirmDialog from "../../../Dialog/Binary/DeleteBinaryConfirmDialog";
import {Apple, DeviceUnknown, Download} from "@mui/icons-material";
import {red} from "@mui/material/colors";
import EditBinaryDialog from "../../../Dialog/Binary/EditBinaryDialog";

interface Props {
    config: Config
}

const TabBinaryView : React.FC<Props> = ({config}) => {

    const apiClient = new ApiClient()
    const dispatch = useDispatch()
    const [openState, setOpenState] = useState(false)
    const [openDlgState, setOpenDlgState] = useState(false)

    const [deleteBinary, setDeleteBinary] = useState<Binary>(initialBinary)
    const [openDeleteDlgState, setOpenDeleteDlgState] = useState(false)
    const [editBinary, setEditBinary] = useState<Binary>(initialBinary)
    const [openEditDlgState, setOpenEditDlgState] = useState(false)

    const history = useHistory()

    const handleOnDownload = (binary:Binary) => {
        (async () => {
            const apiClient = new ApiClient()
            apiClient.downloadFile(config.orgId, binary.fileId, binary.name)
        })()
    }

    const binaries : Binary[] = useSelector(binariesSelectors.selectAll).filter((binary: Binary) => {
        return binary.configId === config.id
    })

    const snapshots : Snapshot[] = useSelector(snapshotsSelectors.selectAll).filter((snapshot: Snapshot) => {
        return snapshot.configId === config.id
    })

    useEffect(() => {
        const binaries = apiClient.fetchBinariesAndRedux(dispatch, config)
        if (binaries === undefined) {
            history.push(`/orgs/${config.orgId}/projects/${config.projectId}/configs/${config.id}`)
        }
        const snapshots = apiClient.fetchSnapshotsAndRedux(dispatch, config)
        if (snapshots === undefined) {
            history.push(`/orgs/${config.orgId}/projects/${config.projectId}/configs/${config.id}`)
        }
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
                                <TableCell style={{fontWeight:"bold"}}>ファイル名</TableCell>
                                <TableCell style={{fontWeight:"bold"}} width="auto">概要</TableCell>
                                <TableCell style={{fontWeight:"bold"}}>アップロード者</TableCell>
                                <TableCell style={{fontWeight:"bold"}}>アップロード時刻</TableCell>
                                <TableCell style={{fontWeight:"bold"}} align="center" width="20px">Download</TableCell>
                                <TableCell style={{fontWeight:"bold"}} align="center" width="20px">編集</TableCell>
                                <TableCell style={{fontWeight:"bold"}} align="center" width="20px">削除</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {
                                binaries.sort((a:Binary, b:Binary) => { return a!.uploadedAt! - b!.uploadedAt! }).map((binary) => {
                                    return (
                                        <TableRow key={binary.id}>
                                            <TableCell>
                                                <IconText
                                                    icon={
                                                        <div style={{display:"flex",alignItems:"center",justifyContent:"center"}}>
                                                            { (binary.name.endsWith(".apk") || binary.name.endsWith(".apks")) && <Android style={{color: green[300]}}/> }
                                                            { binary.name.endsWith(".ipa") && <Apple /> }
                                                            { !binary.name.endsWith(".apk") && !binary.name.endsWith(".apks") && !binary.name.endsWith(".ipa") && <DeviceUnknown style={{color: red[300]}}/> }
                                                        </div>
                                                    }
                                                    text={
                                                        <Link href="#" color="inherit" onClick={() => handleOnDownload(binary)} >
                                                            {binary.name}
                                                        </Link>
                                                    }
                                                />
                                            </TableCell>
                                            <TableCell width="auto">
                                                { binary.description }
                                            </TableCell>
                                            <TableCell>
                                                <AccountName accountId={binary.uploadedBy} />
                                            </TableCell>
                                            <TableCell>
                                                {
                                                    new PHDate(binary.uploadedAt && binary.uploadedAt).toString()
                                                }
                                            </TableCell>
                                            <TableCell align="center" width="20px">
                                                <IconButton onClick={() => handleOnDownload(binary)} >
                                                    <Download style={{color:green[600]}}/>
                                                </IconButton>
                                            </TableCell>
                                            <TableCell align="center" width="20px">
                                                <IconButton
                                                    onClick={() => {
                                                        setEditBinary(binary)
                                                        setOpenEditDlgState(true)
                                                    }}
                                                >
                                                    <Edit style={{color:blue[600]}}/>
                                                </IconButton>
                                            </TableCell>
                                            <TableCell align="center" width="20px">
                                                <IconButton
                                                    onClick={() => {
                                                        setDeleteBinary(binary)
                                                        setOpenDeleteDlgState(true)
                                                    }}
                                                >
                                                    <Delete color="secondary"/>
                                                </IconButton>
                                            </TableCell>
                                        </TableRow>
                                    )
                                })
                            }
                        </TableBody>
                    </Table>
                </TableContainer>
            </div>
            <Snackbar anchorOrigin={{vertical:"top",horizontal:"center"}} open={openState} autoHideDuration={2000} onClose={() => {setOpenState(false)}}>
                <Alert onClose={() => {setOpenState(false)}} severity="success">
                    保存しました
                </Alert>
            </Snackbar>
            <CreateBinaryDialog
                config={config}
                open={openDlgState}
                onClose={() => {setOpenDlgState(false)}}
            />
            <EditBinaryDialog
                open={openEditDlgState}
                binary={editBinary}
                onError={() => {
                    history.push(`/orgs/${config.orgId}/projects/${config.projectId}/configs/${config.id}`)
                }}
                onClose={() => setOpenEditDlgState(false)}
            />
            <DeleteBinaryConfirmDialog
                open={openDeleteDlgState}
                binary={deleteBinary}
                onError={() => {
                    history.push(`/orgs/${config.orgId}/projects/${config.projectId}/configs/${config.id}`)
                }}
                onClose={() => setOpenDeleteDlgState(false)}
            />
        </div>
    )
}

export default TabBinaryView