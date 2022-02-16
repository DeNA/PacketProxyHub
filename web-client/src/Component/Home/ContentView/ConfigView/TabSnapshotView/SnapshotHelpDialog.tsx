/*
 * Copyright 2022 DeNA Co., Ltd.
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

import {CodeSnippet} from "../../../../../Common/Code/Code";
import React from "react";
import {Dialog, Divider, Typography} from "@material-ui/core";

interface SnapshotHelpDialogProps {
    open: boolean
    onDeleteDone?: () => void
    onError?: () => void
    onClose: () => void
}

const HowToDownload = () => {
    const cmdline = `$ curl -LO https://github.com/DeNA/PacketProxyHub/archive/refs/heads/master.zip
$ unzip master.zip
$ cd PacketProxyHub-master/scripts/snapshot
$ rake install`
    return <CodeSnippet value={cmdline} mode="shell" />
}

const HowToCreatSnapshotTar = () => {
    const cmdline = `$ snapshot create`
    return <CodeSnippet value={cmdline} mode="shell" />
}

const HowToExtract = () => {
    const cmdline = `$ snapshot extract downloaded.snapshot`
    return <CodeSnippet value={cmdline} mode="shell" />
}

const SnapshotHelpDialog = ({open, onDeleteDone, onError, onClose}:SnapshotHelpDialogProps) => {
    return <Dialog open={open} onClose={onClose} fullWidth={true} maxWidth="lg">
        <div style={{margin:"1em"}}>
            <Typography variant="h5"><b>利用条件</b></Typography>
            <Typography variant="body2" style={{margin:"1.5em 0 0.5em 0"}}>
                現在のところM1 Macのみサポートしています
            </Typography>
            <Divider style={{margin:"2.5em 0 1em 0"}}/>
            <Typography variant="h5"><b>アップロード方法</b></Typography>
            <Typography variant="body1" style={{margin:"1em 0 0.5em 0"}}>
                <b>1. snapshotコマンドをインストールします</b>
            </Typography>
            <HowToDownload />
            <Typography variant="body1" style={{margin:"1em 0 0.5em 0"}}>
                <b>2. snapshotコマンドを使ってAVDのスナップショットイメージからスナップショットファイルを生成します</b><br/>
                <Typography variant="body2">
                    あらかじめAndroid EmulatorのAVD内でsnapshot機能でスナップショットイメージを取っておく必要があります<br/>
                    snapshotコマンドは対話的に既存のAVDのスナップショットを選択できるようになっているので指示にしたがってスナップショットファイルを生成してください<br/>
                </Typography>
            </Typography>
            <HowToCreatSnapshotTar />
            <Typography variant="body1" style={{margin:"1em 0 0.5em 0"}}>
                <b>3. 生成されたスナップショットファイルをアップロードしてください</b>
            </Typography>
            <Divider style={{margin:"2.5em 0 1em 0"}}/>
            <Typography variant="h5"><b>ダウンロードおよび利用方法</b></Typography>
            <Typography variant="body1" style={{margin:"1em 0 0.5em 0"}}>
                <b>1. 利用したいスナップショットファイルをダウンロードします</b>
            </Typography>
            <Typography variant="body1" style={{margin:"1em 0 0.5em 0"}}>
                <b>2. snapshotコマンドで展開します</b><br/>
                <Typography variant="body2">
                    snapshotコマンドのインストールについては、アップロード方法を参照してください
                </Typography>
            </Typography>
            <HowToExtract />
            <Typography variant="body1" style={{margin:"1em 0 0.5em 0"}}>
                <b>3. 展開されたらAndroid EmulatorでAVDを起動し設定画面からスナップショットを開いてください</b>
            </Typography>
        </div>
    </Dialog>
}

export default SnapshotHelpDialog