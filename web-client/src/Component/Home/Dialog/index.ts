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

import CreateOrgDialog from "./Org/CreateOrgDialog";

import CreateProjectDialog from "./Project/CreateProjectDialog";
import EditProjectDialog from "./Project/EditProjectDialog";
import DeleteProjectConfirmDialog from "./Project/DeleteProjectConfirmDialog";

import CreateOrgMemberDialog from "./OrgMember/CreateOrgMemberDialog";
import DeleteOrgMemberConfirmDialog from "./OrgMember/DeleteOrgMemberConfirmDialog";

import EditConfigDialog from "./Config/EditConfigDialog";
import DeleteConfigConfirmDialog from "./Config/DeleteConfigConfirmDialog";

import EditAccountDialog from "./Account/EditAccountDialog";

export {
    CreateOrgDialog,
    CreateProjectDialog,
    CreateOrgMemberDialog,

    EditAccountDialog,
    EditProjectDialog,
    EditConfigDialog,

    DeleteProjectConfirmDialog,
    DeleteConfigConfirmDialog,
    DeleteOrgMemberConfirmDialog,
}