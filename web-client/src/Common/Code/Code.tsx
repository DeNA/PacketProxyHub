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
import {Controlled as CodeMirror} from "react-codemirror2";

require('codemirror/lib/codemirror.css');
require('codemirror/theme/material.css');
require('codemirror/mode/javascript/javascript');
require('codemirror/mode/shell/shell');
require('./CodeMirror.css')

interface Props {
    value: string
    mode: string
    onChange: (editor: CodeMirror.Editor, data: CodeMirror.EditorChange, value: string) => void
}

const Code : React.FC<Props> = ({value, mode, onChange}) => {
    return (
        <CodeMirror
            editorDidMount={editor => {
                editor.setSize("100%", "auto")
            }}
            value={value}
            options={{
                theme: 'material',
                mode: mode,
            }}
            onBeforeChange={onChange}
        />
    )
}

interface CodeSnippetProps {
    value: string
    mode: string
}

export const CodeSnippet : React.FC<CodeSnippetProps> = ({value, mode}) => {
    return (
        <CodeMirror
            editorDidMount={editor => {
                editor.setSize("auto", "auto")
                editor.isReadOnly()
            }}
            value={value}
            options={{
                theme: 'material',
                mode: mode,
                readOnly: true,
                lineWrapping: false,
            }}
            onBeforeChange={() => {}}
        />
    )
}

export default Code