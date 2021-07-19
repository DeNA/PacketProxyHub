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

import React, {useEffect, useState} from "react"
import {Divider, List, ListItem, TextField} from "@material-ui/core";

interface Props {
    label: string
    items: string[]
    value: string
    onMatched: (input : string) => void
    onUnmatched: (input : string) => void
}

const SearchableList : React.FC<Props> = ({label, items, value, onMatched, onUnmatched}) => {

    const [text, setText] = useState("")
    const [itemCands, setItemCands] = useState(items)
    const [matched, setMatched] = useState(false)

    useEffect(() => {
        setText(value)
    }, [value])

    useEffect(() => {
        const newItemCands = items.filter(item => item.indexOf(text) > -1)
        if (newItemCands.find((itemCand) => itemCand === text)) {
            setMatched(true)
            onMatched(text)
        } else {
            setMatched(false)
            onUnmatched(text)
        }
        setItemCands(newItemCands)
    },[text,items])

    const handleOnChange = (event: React.ChangeEvent<{ value: unknown }>) => {
        setText(event.target.value as string)
    }

    const handleOnSelected = (itemCand: string) => {
        setText(itemCand)
    }

    return (
        <div style={{width:"100%"}}>
            <div style={{display:"flex"}}>
                <div style={{width:"100%"}}>
                    <TextField fullWidth={true} label={label} onChange={handleOnChange} value={text} style={{width:"100%",padding:"0",margin:"0 .5em 0 0",backgroundColor:matched?"lightgreen":"lightpink"}}/>
                    { !matched && (
                        <div style={{borderRadius:"5px",border:"1px solid gray",maxHeight:"100px",overflowY:"hidden"}} >
                            <List style={{padding:"0",margin:"0",maxHeight:"100px",overflowY:"auto"}}>
                                { itemCands.map(itemCand =>
                                    <div>
                                        <ListItem button onClick={() => handleOnSelected(itemCand)}>
                                            {itemCand}
                                        </ListItem>
                                        <Divider />
                                    </div>
                                )}
                            </List>
                        </div>
                    )}
                </div>
            </div>
        </div>
    )
}

export default SearchableList