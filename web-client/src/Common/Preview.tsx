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

import React from "react";
import Resizer from "react-image-file-resizer";

export const resizeFile = (file: Blob): Promise<string> => {
    return new Promise((resolve) => {
        Resizer.imageFileResizer(
            file,
            100,
            500,
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

interface Props {
    resizedFile: string
}

const Preview : React.FC<Props> = ({resizedFile}) => {
    return (
        <div>
            <img src={resizedFile} />
        </div>
    )
}

export default Preview