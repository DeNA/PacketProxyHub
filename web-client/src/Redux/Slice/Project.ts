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

import {createEntityAdapter, createSlice, PayloadAction} from "@reduxjs/toolkit";
import {RootState} from "../store";

export interface Project {
    orgId: string
    id: string
    name: string
    description: string
    content: string
    updatedAt?: number
    openState: boolean
}

export const initialProject = {
    orgId: "",
    id: "",
    name: "",
    description: "",
    content: "",
    openState: false,
}

export const projectsAdapter = createEntityAdapter<Project>({
    selectId: (project) => project.id,
    sortComparer: (a, b) => a.name.localeCompare(b.name),
})

export const project = createSlice({
    name: "projects",
    initialState: projectsAdapter.getInitialState(),
    reducers: {
        projectsReset: (state) => projectsAdapter.getInitialState(),
        projectsUpdate: projectsAdapter.updateOne,
        projectsRemove: projectsAdapter.removeMany,
        projectsUpsert: projectsAdapter.upsertMany,
        projectsSync(state, action:PayloadAction<{orgId:string,projects:Project[]}>) {
            const oldProjects = projectsAdapter.getSelectors().selectAll(state).filter(p => p.orgId === action.payload.orgId)
            let newProjects = action.payload.projects
            oldProjects.forEach(old => {
                const found = newProjects.find(project => project.id === old.id)
                if (found) {
                    projectsAdapter.updateOne(state, {id: old.id, changes : found})
                    newProjects = newProjects.filter(project => project.id !== old.id)
                } else {
                    projectsAdapter.removeOne(state, old.id)
                }
            })
            projectsAdapter.upsertMany(state, newProjects)
        }
    }
})

export const {
    projectsReset,
    projectsUpdate,
    projectsRemove,
    projectsSync,
    projectsUpsert,
} = project.actions

export const projectsSelectors = projectsAdapter.getSelectors<RootState>(
    (state) => state.projects
)

export const selectProjectByName = (state: RootState, name : string) : Project[] => {
    const projects : Project[] = projectsSelectors.selectAll(state)
    return projects.filter((project) => name === project.name)
}
