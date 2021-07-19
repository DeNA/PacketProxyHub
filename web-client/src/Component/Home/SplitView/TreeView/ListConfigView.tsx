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

import React from "react"
import {Config} from "../../../../Redux";
import {ListItem} from "@material-ui/core";
import {SettingsApplications} from "@material-ui/icons";
import {purple} from "@material-ui/core/colors";

interface Props {
    selectedId: string
    config: Config
    onClickConfig: (config: Config) => void
}

const ListConfigView : React.FC<Props> = ({selectedId, config, onClickConfig}) => {
    return (
        <div>
            <ListItem
                button
                selected={config.id === selectedId}
                style={{display:"flex",justifyContent:"flex-start",alignItems:"center",width:"100%",padding:"2px 2px 0 4.8em"}}
                onClick={() => { onClickConfig(config) }}
            >
                <SettingsApplications style={{color:purple[500]}}/>
                <span style={{fontSize:"14px",width:"100%",textAlign:"left",margin:"0 .2em"}}>
                        {config.name}
                </span>
            </ListItem>
        </div>
    )
}

export default ListConfigView
