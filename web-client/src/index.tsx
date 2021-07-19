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

import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import {Provider} from 'react-redux';
import {BrowserRouter as Router, Route, Switch} from "react-router-dom"
import {Auth, SignIn} from "./Component";
import {createMuiTheme, MuiThemeProvider} from "@material-ui/core";
import {blue} from "@material-ui/core/colors";
import {store} from "./Redux/store";

const customTheme = createMuiTheme({
    mixins: {
        toolbar: {
            minHeight: 42
        }
    },
    palette: {
        // primary: blue
        primary: {
            main: blue[300]
        }
    },
    props: {
        MuiCheckbox: {
            color: "primary"
        },
        MuiList: {
            dense: false
        },
        MuiRadio: {
            color: "primary"
        },
        MuiSwitch: {
            color: "primary"
        },
        MuiTable: {
            size: "small"
        },
        MuiTextField: {
            variant: "outlined"
            //InputProps: {
            //    style: {
            //        height: 38
            //    }
            //}
        },
    },
    typography: {
        fontSize: 14,
        button: {
            textTransform: "none"
        }
    }
})

ReactDOM.render(
    <Provider store={store}>
        <MuiThemeProvider theme={customTheme}>
            <Router>
                <Switch>
                    <Route exact path={"/signin"} component={SignIn} />
                    <Route path={"/"} component={Auth} />
                </Switch>
            </Router>
        </MuiThemeProvider>
    </Provider>,
    document.getElementById("root")
)
