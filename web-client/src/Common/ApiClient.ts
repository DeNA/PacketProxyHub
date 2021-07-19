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

import {
    Account,
    accountsUpsert,
    Config,
    configsRemove,
    configsSync,
    configsUpsert,
    configUpdate,
    Org,
    OrgMember,
    orgMembersDelete,
    orgMembersSync,
    orgMembersUpsert,
    orgMemberUpdate,
    orgsRemove,
    orgsSync,
    orgsUpsert,
    Project,
    projectsRemove,
    projectsSync,
    projectsUpsert,
    setMe,
    treeOpenStatesAdd,
    treeOpenStatesUpsert,
} from "../Redux"
import {Dispatch} from "react";

class ApiClient {

    static getApiServer = () : string|undefined => {
        return process.env.NODE_ENV === "development"
            ? "http://localhost:1234"
            : process.env.REACT_APP_API_SERVER_URL
    }

    // Resource
    public async fetchAllAndRedux(dispatch: Dispatch<any>) : Promise<void> {
        const resource = await this.fetchAll()
        const accounts : Account[] = await Promise.all(resource.accounts.map(async(account: Account) => {
            return account
        }))
        dispatch(accountsUpsert(accounts))

        dispatch(orgsUpsert(resource.orgs))
        dispatch(treeOpenStatesUpsert(resource.orgs.map((org:Org) => {return {id:org.id, openState:true}})))

        const projects : Project[] = await Promise.all(resource.projects.map(async(stProject: any) => {
            return Promise.all(stProject.projects.map(async(project: Project) => {
                project.orgId = stProject.orgId
                return project
            }))
        }))
        dispatch(projectsUpsert(projects.flat()))
        dispatch(treeOpenStatesUpsert(projects.flat().map(project => {return {id:project.id, openState:false}})))

        const configs : Config[] = await Promise.all(resource.configs.map(async(stConfig: any) => {
            return Promise.all(stConfig.configs.map(async(config: Config) => {
                config.orgId = stConfig.orgId
                config.projectId = stConfig.projectId
                return config
            }))
        }))
        dispatch(configsUpsert(configs.flat()))

        const orgMembers : OrgMember[] = await Promise.all(resource.orgMembers.map(async(stOrgMember: any) => {
            return Promise.all(stOrgMember.orgMembers.map(async(orgMember: OrgMember) => {
                orgMember.orgId = stOrgMember.orgId
                return orgMember
            }))
        }))
        dispatch(orgMembersUpsert(orgMembers.flat()))
    }
    public async fetchAll() : Promise<any> {
        return this.callApi("GET", "/all")
    }

    // Accounts
    public async listAccountIds() : Promise<any[]> {
        return this.callApi("GET", `/accounts`)
    }
    public async editMyAccount(account: Account) : Promise<void> {
        return this.callApi("PUT", `/accounts/me`, JSON.stringify(account))
    }
    public async fetchMyAccountAndRedux(dispatch:Dispatch<any>) : Promise<Account|undefined> {
        try {
            const me = await this.fetchMyAccount();
            dispatch(setMe(me))
            return me
        } catch(e) {
            return undefined
        }
    }
    public async fetchMyAccount() : Promise<Account> {
        return this.callApi("GET", `/accounts/me`)
    }
    public async fetchAccount(accountId: string) : Promise<Account> {
        return this.callApi("GET", `/accounts/${accountId}`)
    }
    public async searchAccount(nameKey: string) : Promise<Account[]> {
        return this.callApi("GET", `/accounts/search?nameKey=${nameKey}`)
    }

    // Projects
    public async createProjectAndRedux(dispatch:Dispatch<any>, orgId:string, name:string, description:string, content?:string) : Promise<Project|undefined> {
        try {
            const projectId = await this.createProject(orgId, name, description, content)
            const rawProject = await this.fetchProject(orgId, projectId)
            const newProject: Project = {...rawProject, orgId, projectId}
            dispatch(projectsUpsert([newProject]))
            dispatch(treeOpenStatesUpsert([{id: projectId, openState: false}]))
            return newProject
        } catch(e) {
            return undefined
        }
    }
    public async createProject(orgId:string, name:string, description:string, content?:string) : Promise<any> {
        return this.callApi("POST", `/orgs/${orgId}/projects/`, JSON.stringify({name, description, content}))
    }
    public async editProjectAndRedux(dispatch:Dispatch<any>, project:Project) : Promise<Project|undefined> {
        try {
            await this.editProject(project)
            return this.fetchProjectAndRedux(dispatch, project)
        } catch(e) {
            return undefined
        }
    }
    public async editProject(project: Project) : Promise<void> {
        return this.callApi("PUT", `/orgs/${project.orgId}/projects/${project.id}`, JSON.stringify(project))
    }
    public async deleteProjectAndRedux(dispatch:Dispatch<any>, project:Project) : Promise<void|undefined> {
        try {
            await this.deleteProject(project)
            dispatch(projectsRemove([project.id]))
        } catch (e) {
            return undefined
        }
    }
    public async deleteProject(project:Project) : Promise<void> {
        return this.callApi("DELETE", `/orgs/${project.orgId}/projects/${project.id}`)
    }
    public async listProjectIds(orgId: string) : Promise<any[]> {
        return this.callApi("GET", `/orgs/${orgId}/projects`)
    }
    public async fetchProjectAndRedux(dispatch:Dispatch<any>, project:Project) : Promise<Project|undefined> {
        try {
            const rawProject = await this.fetchProject(project.orgId, project.id)
            const newProject = {...rawProject, orgId: project.orgId}
            dispatch(projectsUpsert([newProject]))
            return newProject
        } catch (e) {
            return undefined
        }
    }
    public async fetchProject(orgId: string, projectId: string) : Promise<any> {
        return this.callApi("GET", `/orgs/${orgId}/projects/${projectId}`)
    }
    public async fetchProjectsAndRedux(dispatch:Dispatch<any>, orgId:string) : Promise<Project[]|undefined> {
        try {
            const rawProjects = await this.fetchProjects(orgId)
            const newProjects = rawProjects.map(rawProject => {
                return {...rawProject, orgId: orgId}
            })
            dispatch(projectsSync({orgId: orgId, projects: newProjects}))
            dispatch(treeOpenStatesAdd(newProjects.map(p => { return {id: p.id, openState: false}})))
            return newProjects
        } catch (e) {
            return undefined;
        }
    }
    public async fetchProjects(orgId: string): Promise<any[]> {
        return this.callApi("GET", `/orgs/${orgId}/projects/all`)
    }

    // Configs
    public async createConfigAndRedux(dispatch:Dispatch<any>, orgId:string, projectId:string, name:string, description:string, packetProxyConf?:string, pfConf?:string, memo?:string) : Promise<Config|undefined> {
        try {
            const configId = await this.createConfig(orgId, projectId, name, description, packetProxyConf, pfConf, memo)
            const newConfig: Config = await this.fetchConfig(orgId, projectId, configId)
            const newConfigRedux = {...newConfig, orgId, projectId}
            dispatch(configsUpsert([newConfigRedux]))
            return newConfigRedux
        } catch(e) {
            return undefined
        }
    }
    public async createConfig(orgId:string, projectId:string, name:string, description:string, packetProxyConf?:string, pfConf?:string, memo?:string) : Promise<any> {
        return this.callApi("POST", `/orgs/${orgId}/projects/${projectId}/configs/`, JSON.stringify({
            name, description, packetProxyConf, pfConf, memo
        }))
    }

    public async editConfigAndRedux(dispatch: Dispatch<any>, config: Config) : Promise<Config|undefined>{
        try {
            await this.editConfig(config)
            return this.fetchConfigAndRedux(dispatch, config)
        } catch(e) {
            return undefined
        }
    }
    public async editConfig(config: Config) : Promise<void> {
        return this.callApi("PUT", `/orgs/${config.orgId}/projects/${config.projectId}/configs/${config.id}`, JSON.stringify(config))
    }
    public async deleteConfigAndRedux(dispatch:Dispatch<any>, config:Config) : Promise<void|undefined> {
        try {
            await this.deleteConfig(config)
            dispatch(configsRemove([config.id]))
        } catch(e) {
            return undefined
        }
    }
    public async deleteConfig(config:Config) : Promise<void> {
        return this.callApi("DELETE", `/orgs/${config.orgId}/projects/${config.projectId}/configs/${config.id}`)
    }
    public async listConfigIds(orgId:string, projectId:string) : Promise<any[]> {
        return this.callApi("GET", `/orgs/${orgId}/projects/${projectId}/configs`)
    }
    public async fetchConfigsAndRedux(dispatch:Dispatch<any>, project:Project) : Promise<Config[]|undefined> {
        try {
            const rawConfigs = await this.fetchConfigs(project)
            const newConfigs = rawConfigs.map(rawConfig => {
                return {...rawConfig, orgId: project.orgId, projectId: project.id}
            })
            dispatch(configsSync({projectId: project.id, configs: newConfigs}))
            dispatch(treeOpenStatesAdd(newConfigs.map(c => { return {id: c.id, openState: false}})))
            return newConfigs
        } catch (e) {
            return undefined;
        }
    }
    public async fetchConfigs(project: Project) : Promise<Config[]> {
        return this.callApi("GET", `/orgs/${project.orgId}/projects/${project.id}/configs/all`)
    }
    public async fetchConfigAndRedux(dispatch:Dispatch<any>, config:Config) : Promise<Config|undefined> {
        try {
            const rawConfig = await this.fetchConfig(config.orgId, config.projectId, config.id)
            const newConfig = {...rawConfig, orgId: config.orgId, projectId: config.projectId}
            dispatch(configUpdate({id: config.id, changes: newConfig}))
            return newConfig
        } catch(e) {
            return undefined
        }
    }
    public async fetchConfig(orgId:string, projectId:string, configId:string) : Promise<any> {
        return this.callApi("GET", `/orgs/${orgId}/projects/${projectId}/configs/${configId}`)
    }

    // Orgs
    public async createOrgAndRedux(dispatch:Dispatch<any>, name:string, description:string) : Promise<Org|undefined> {
        try {
            const orgId = await this.createOrg(name, description)
            const rawOrg = await this.fetchOrg(orgId)
            const newOrg = {...rawOrg}
            dispatch(orgsUpsert([newOrg]))
            dispatch(treeOpenStatesUpsert([{id: orgId, openState: true}]))
            return newOrg
        } catch(e) {
            return undefined
        }
    }
    public async createOrg(name: string, description: string): Promise<any> {
        const body = {name, description}
        return this.callApi("POST", "/orgs/", JSON.stringify(body))
    }
    public async editOrgAndRedux(dispatch:Dispatch<any>, org:Org) : Promise<Org|undefined> {
        try {
            await this.editOrg(org)
            return this.fetchOrgAndRedux(dispatch, org)
        } catch(e) {
            return undefined
        }
    }
    public async editOrg(org:Org) : Promise<any[]> {
        return this.callApi("PUT", `/orgs/${org.id}`, JSON.stringify(org))
    }
    public async deleteOrgAndRedux(dispatch:Dispatch<any>, org:Org) : Promise<void|undefined> {
        try {
            await this.deleteOrg(org)
            dispatch(orgsRemove([org.id]))
        } catch(e) {
            return undefined
        }
    }
    public async deleteOrg(org:Org) : Promise<void> {
        return this.callApi("DELETE", `/orgs/${org.id}`)
    }
    public async listOrgIds(): Promise<any[]> {
        return this.callApi("GET", "/orgs")
    }
    public async fetchOrgsAndRedux(dispatch:Dispatch<any>) : Promise<Org[]|undefined> {
        try {
            const newOrgs = await this.fetchOrgs()
            dispatch(orgsSync(newOrgs))
            dispatch(treeOpenStatesAdd(newOrgs.map(o => { return {id: o.id, openState: false}})))
            return newOrgs
        } catch (e) {
            return undefined;
        }
    }
    public async fetchOrgs(): Promise<any[]> {
        return this.callApi("GET", `/orgs/all`)
    }
    public async fetchOrgAndRedux(dispatch:Dispatch<any>, org:Org) : Promise<Org|undefined> {
        try {
            const newOrg = await this.fetchOrg(org.id)
            dispatch(orgsUpsert([newOrg]))
            return newOrg
        } catch(e) {
            return undefined
        }
    }
    public async fetchOrg(orgId: string) : Promise<any> {
        return this.callApi("GET", `/orgs/${orgId}`)
    }
    public async fetchOrgNames(): Promise<any[]> {
        const orgIds: Array<any> = await this.callApi("GET", "/orgs")
        return Promise.all(
            orgIds.map(
                async (orgId) => {
                    return this.callApi("GET", `/orgs/${orgId.id}`)
                }))
    }

    // OrgMembers
    public async listOrgMemberIds(orgId: string): Promise<any[]> {
        return this.callApi("GET", `/orgs/${orgId}/members`)
    }
    public async fetchOrgMembersAndRedux(dispatch:Dispatch<any>, orgId:string) : Promise<OrgMember[]|undefined> {
        try {
            const rawOrgMembers = await this.fetchOrgMembers(orgId)
            const newOrgMembers = rawOrgMembers.map(rawOrgMember => {
                return {...rawOrgMember, orgId: orgId}
            })
            dispatch(orgMembersSync({orgId: orgId, orgMembers: newOrgMembers}))
            return newOrgMembers
        } catch (e) {
            return undefined;
        }
    }
    public async fetchOrgMembers(orgId: string) : Promise<OrgMember[]> {
        return this.callApi("GET", `/orgs/${orgId}/members/all`)
    }
    public async fetchOrgMemberAndRedux(dispatch:Dispatch<any>, orgMember:OrgMember) : Promise<OrgMember|undefined> {
        try {
            const rawOrgMember = await this.fetchOrgMember(orgMember.orgId, orgMember.id)
            const newOrgMember = {...rawOrgMember, orgId: orgMember.orgId}
            dispatch(orgMemberUpdate({id: orgMember.id, changes: newOrgMember}))
            return newOrgMember
        } catch(e) {
            return undefined
        }
    }
    public async fetchOrgMember(orgId: string, orgMemberId: string) : Promise<OrgMember> {
        return this.callApi("GET", `/orgs/${orgId}/members/${orgMemberId}`)
    }
    public async createOrgMemberAndRedux(dispatch:Dispatch<any>, orgId:string, accountId:string, role:string) : Promise<OrgMember|undefined> {
        try {
            const orgMemberId = await this.createOrgMember(orgId, accountId, role)
            const rawOrgMember = await this.fetchOrgMember(orgId, orgMemberId)
            const newOrgMember: OrgMember = {...rawOrgMember, orgId}
            dispatch(orgMembersUpsert([newOrgMember]))
            return newOrgMember
        } catch(e) {
            return undefined
        }
    }
    public async createOrgMember(orgId: string, accountId: string, role: string): Promise<string> {
        const body = {accountId, role}
        return this.callApi("POST", `/orgs/${orgId}/members/`, JSON.stringify(body))
    }
    public async deleteOrgMemberAndRedux(dispatch:Dispatch<any>, orgMember:OrgMember) : Promise<void|undefined> {
        try {
            await this.deleteOrgMember(orgMember)
            dispatch(orgMembersDelete([orgMember.id]))
        } catch(e) {
            return undefined
        }
    }
    public async deleteOrgMember(orgMember:OrgMember) : Promise<any[]> {
        return this.callApi("DELETE", `/orgs/${orgMember.orgId}/members/${orgMember.id}`)
    }
    public async editOrgMemberAndRedux(dispatch:Dispatch<any>, orgMember:OrgMember) : Promise<OrgMember|undefined> {
        try {
            await this.editOrgMember(orgMember)
            const rawOrgMember = await this.fetchOrgMember(orgMember.orgId, orgMember.id)
            const newOrgMember = {...rawOrgMember, orgId: orgMember.orgId}
            dispatch(orgMemberUpdate({id: orgMember.id, changes: newOrgMember}))
            return newOrgMember
        } catch(e) {
            return undefined
        }
    }
    public async editOrgMember(orgMember: OrgMember) : Promise<void> {
        return this.callApi("PUT", `/orgs/${orgMember.orgId}/members/${orgMember.id}`, JSON.stringify(orgMember))
    }

    private async callApi(method: string, path: string, jsonBody?: string): Promise<any> {
        let headers: HeadersInit = {
        }
        let params: RequestInit = {
            method: method,
            headers: headers,
            credentials: "include",
        }
        if (jsonBody && jsonBody.length > 0) {
            headers["Content-Type"] = "application/json"
            params.body = jsonBody
        }
        const response = await fetch(`${ApiClient.getApiServer()}${path}`, params)
        if (!response.ok) {
            throw new Error(`HTTP status = ${response.statusText}`)
        }
        return response.json()
    }
}

export default ApiClient