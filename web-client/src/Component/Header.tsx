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
import {AppBar, Button, Link, Toolbar, Typography} from "@material-ui/core";
import {ExitToApp} from "@material-ui/icons";
import {useDispatch, useSelector} from "react-redux";
import {resetRedux, selectMe, signOut} from "../Redux";
import {EditAccountDialog} from "./Home/Dialog";
import {useHistory} from "react-router-dom";

const Header : React.FC = () => {

    const dispatch = useDispatch()
    const history = useHistory()
    const me = useSelector(selectMe)

    const [openDlgState, setOpenDlgState] = useState(false)

    return (
        <div>
            <AppBar position="static">
                <Toolbar variant="dense">
                    <Typography variant="h5">
                        PacketProxy<strong>Hub</strong>
                    </Typography>
                    <div style={{marginLeft:"auto"}}>
                        <Link href="#" onClick={() => setOpenDlgState(true)} style={{color:"black",margin:"0 1em"}} >
                            { me.name }
                        </Link>
                        <Button
                            variant="outlined"
                            size="small"
                            color="inherit"
                            endIcon={<ExitToApp />}
                            onClick={async () => {
                                await signOut()
                                dispatch(resetRedux)
                                history.push("/")
                            }}
                        >
                            Logout
                        </Button>
                    </div>
                </Toolbar>
            </AppBar>
            <EditAccountDialog open={openDlgState} onClose={() => setOpenDlgState(false)} />
        </div>
    )
}

export default Header