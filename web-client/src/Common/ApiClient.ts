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
    binariesRemove,
    binariesSync,
    binariesUpsert,
    Binary,
    snapshotsRemove,
    snapshotsSync,
    snapshotsUpsert,
    Snapshot,
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
import {resizeFile} from "./Preview";

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

        const binaries : Binary[] = await Promise.all(resource.binaries.map(async(stBinary: any) => {
            return Promise.all(stBinary.binaries.map(async(binary: Binary) => {
                binary.orgId = stBinary.orgId
                binary.projectId = stBinary.projectId
                binary.configId = stBinary.configId
                return binary
            }))
        }))
        dispatch(binariesUpsert(binaries.flat()))

        const snapshots : Snapshot[] = await Promise.all(resource.snapshots.map(async(stSnapshot: any) => {
            return Promise.all(stSnapshot.snapshots.map(async(snapshot: Snapshot) => {
                snapshot.orgId = stSnapshot.orgId
                snapshot.projectId = stSnapshot.projectId
                snapshot.configId = stSnapshot.configId
                let screenshot = await this.downloadFileAsBlob(snapshot.orgId, snapshot.screenshotId)
                screenshot = screenshot.slice(0, screenshot.size, "image/png")
                snapshot.resizedScreenshot = await resizeFile(screenshot)
                return snapshot
            }))
        }))
        dispatch(snapshotsUpsert(snapshots.flat()))
    }
    public async fetchAll() : Promise<any> {
        return (await this.callApi("GET", "/all")).json()
    }

    // Accounts
    public async listAccountIds() : Promise<any[]> {
        return (await this.callApi("GET", `/accounts`)).json()
    }
    public async editMyAccount(account: Account) : Promise<void> {
        return (await this.callApi("PUT", `/accounts/me`, JSON.stringify(account))).json()
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
        return (await this.callApi("GET", `/accounts/me`)).json()
    }
    public async fetchAccount(accountId: string) : Promise<Account> {
        return (await this.callApi("GET", `/accounts/${accountId}`)).json()
    }
    public async searchAccount(nameKey: string) : Promise<Account[]> {
        return (await this.callApi("GET", `/accounts/search?nameKey=${nameKey}`)).json()
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
        return (await this.callApi("POST", `/orgs/${orgId}/projects/`, JSON.stringify({name, description, content}))).json()
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
        return (await this.callApi("PUT", `/orgs/${project.orgId}/projects/${project.id}`, JSON.stringify(project))).json()
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
        return (await this.callApi("DELETE", `/orgs/${project.orgId}/projects/${project.id}`)).json()
    }
    public async listProjectIds(orgId: string) : Promise<any[]> {
        return (await this.callApi("GET", `/orgs/${orgId}/projects`)).json()
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
        return (await this.callApi("GET", `/orgs/${orgId}/projects/${projectId}`)).json()
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
        return (await this.callApi("GET", `/orgs/${orgId}/projects/all`)).json()
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
        return (await this.callApi("POST", `/orgs/${orgId}/projects/${projectId}/configs/`, JSON.stringify({
            name, description, packetProxyConf, pfConf, memo
        }))).json()
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
        return (await this.callApi("PUT", `/orgs/${config.orgId}/projects/${config.projectId}/configs/${config.id}`, JSON.stringify(config))).json()
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
        return (await this.callApi("DELETE", `/orgs/${config.orgId}/projects/${config.projectId}/configs/${config.id}`)).json()
    }
    public async listConfigIds(orgId:string, projectId:string) : Promise<any[]> {
        return (await this.callApi("GET", `/orgs/${orgId}/projects/${projectId}/configs`)).json()
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
        return (await this.callApi("GET", `/orgs/${project.orgId}/projects/${project.id}/configs/all`)).json()
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
        return (await this.callApi("GET", `/orgs/${orgId}/projects/${projectId}/configs/${configId}`)).json()
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
        return (await this.callApi("POST", "/orgs/", JSON.stringify(body))).json()
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
        return (await this.callApi("PUT", `/orgs/${org.id}`, JSON.stringify(org))).json()
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
        return (await this.callApi("DELETE", `/orgs/${org.id}`)).json()
    }
    public async listOrgIds(): Promise<any[]> {
        return (await this.callApi("GET", "/orgs")).json()
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
        return (await this.callApi("GET", `/orgs/all`)).json()
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
        return (await this.callApi("GET", `/orgs/${orgId}`)).json()
    }
    public async fetchOrgNames(): Promise<any[]> {
        const orgIds: Array<any> = await (await this.callApi("GET", "/orgs")).json()
        return Promise.all(
            orgIds.map(
                async (orgId) => {
                    return (await this.callApi("GET", `/orgs/${orgId.id}`)).json()
                }))
    }

    // OrgMembers
    public async listOrgMemberIds(orgId: string): Promise<any[]> {
        return (await this.callApi("GET", `/orgs/${orgId}/members`)).json()
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
        return (await this.callApi("GET", `/orgs/${orgId}/members/all`)).json()
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
        return (await this.callApi("GET", `/orgs/${orgId}/members/${orgMemberId}`)).json()
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
        return (await this.callApi("POST", `/orgs/${orgId}/members/`, JSON.stringify(body))).json()
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
        return (await this.callApi("DELETE", `/orgs/${orgMember.orgId}/members/${orgMember.id}`)).json()
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
        return (await this.callApi("PUT", `/orgs/${orgMember.orgId}/members/${orgMember.id}`, JSON.stringify(orgMember))).json()
    }

    // Binaries
    public async createBinaryAndRedux(dispatch:Dispatch<any>, orgId:string, projectId:string, configId:string, name:string, description:string, fileId:string) : Promise<Binary|undefined> {
        try {
            const binaryId = await this.createBinary(orgId, projectId, configId, name, description, fileId)
            const newBinary: Binary = await this.fetchBinary(orgId, projectId, configId, binaryId)
            const newBinaryRedux = {...newBinary, orgId, projectId, configId}
            dispatch(binariesUpsert([newBinaryRedux]))
            return newBinaryRedux
        } catch(e) {
            return undefined
        }
    }
    public async createBinary(orgId:string, projectId:string, configId:string, name:string, description:string, fileId:string) : Promise<any> {
        return (await this.callApi("POST", `/orgs/${orgId}/projects/${projectId}/configs/${configId}/binaries/`, JSON.stringify({
            name, description, fileId
        }))).json()
    }
    public async fetchBinaryAndRedux(dispatch:Dispatch<any>, binary:Binary) : Promise<Binary|undefined> {
        try {
            const rawBinary = await this.fetchBinary(binary.orgId, binary.projectId, binary.configId, binary.id)
            const newBinary = {...rawBinary, orgId: binary.orgId, projectId: binary.projectId, configId: binary.configId}
            dispatch(configUpdate({id: binary.id, changes: newBinary}))
            return newBinary
        } catch(e) {
            return undefined
        }
    }
    public async fetchBinary(orgId:string, projectId:string, configId:string, binaryId:string) : Promise<any> {
        return (await this.callApi("GET", `/orgs/${orgId}/projects/${projectId}/configs/${configId}/binaries/${binaryId}`)).json()
    }
    public async fetchBinariesAndRedux(dispatch:Dispatch<any>, config:Config) : Promise<Binary[]|undefined> {
        try {
            const rawBinaries = await this.fetchBinaries(config)
            const newBinaries = rawBinaries.map(rawBinary => {
                return {...rawBinary, orgId: config.orgId, projectId: config.projectId, configId: config.id}
            })
            dispatch(binariesSync({configId: config.id, binaries: newBinaries}))
            dispatch(treeOpenStatesAdd(newBinaries.map(c => { return {id: c.id, openState: false}})))
            return newBinaries
        } catch (e) {
            return undefined;
        }
    }
    public async fetchBinaries(config: Config) : Promise<Binary[]> {
        return (await this.callApi("GET", `/orgs/${config.orgId}/projects/${config.projectId}/configs/${config.id}/binaries/all`)).json()
    }
    public async editBinaryAndRedux(dispatch: Dispatch<any>, binary: Binary) : Promise<Binary|undefined>{
        try {
            await this.editBinary(binary)
            return this.fetchBinaryAndRedux(dispatch, binary)
        } catch(e) {
            return undefined
        }
    }
    public async editBinary(binary: Binary) : Promise<void> {
        return (await this.callApi("PUT", `/orgs/${binary.orgId}/projects/${binary.projectId}/configs/${binary.configId}/binaries/${binary.id}`, JSON.stringify(binary))).json()
    }
    public async deleteBinaryAndRedux(dispatch:Dispatch<any>, binary:Binary) : Promise<void|undefined> {
        try {
            const status = await this.deleteBinary(binary)
            dispatch(binariesRemove([binary.id]))
            return status
        } catch(e) {
            return undefined
        }
    }
    public async deleteBinary(binary:Binary) : Promise<void> {
        return (await this.callApi("DELETE", `/orgs/${binary.orgId}/projects/${binary.projectId}/configs/${binary.configId}/binaries/${binary.id}`)).json()
    }

    // Snapshot
    public async createSnapshotAndRedux(dispatch:Dispatch<any>, orgId:string, projectId:string, configId:string, name:string, description:string, androidVersion:string, googlePlay:number, fileId:string, screenshotId:string) : Promise<Snapshot|undefined> {
        try {
            const snapshotId = await this.createSnapshot(orgId, projectId, configId, name, description, androidVersion, googlePlay, fileId, screenshotId)
            const newSnapshot: Snapshot = await this.fetchSnapshot(orgId, projectId, configId, snapshotId)
            let screenshot = await this.downloadFileAsBlob(orgId, screenshotId)
            screenshot = screenshot.slice(0, screenshot.size, "image/png")
            const resizedScreenshot = await resizeFile(screenshot)
            const newSnapshotRedux = {...newSnapshot, orgId, projectId, configId, resizedScreenshot}
            dispatch(snapshotsUpsert([newSnapshotRedux]))
            return newSnapshotRedux
        } catch(e) {
            return undefined
        }
    }
    public async createSnapshot(orgId:string, projectId:string, configId:string, name:string, description:string, androidVersion:string, googlePlay:number, fileId:string, screenshotId:string) : Promise<any> {
        return (await this.callApi("POST", `/orgs/${orgId}/projects/${projectId}/configs/${configId}/snapshots/`, JSON.stringify({
            name, description, androidVersion, googlePlay, fileId, screenshotId
        }))).json()
    }
    public async fetchSnapshotAndRedux(dispatch:Dispatch<any>, snapshot:Snapshot) : Promise<Snapshot|undefined> {
        try {
            const rawSnapshot = await this.fetchSnapshot(snapshot.orgId, snapshot.projectId, snapshot.configId, snapshot.id)
            let screenshot = await this.downloadFileAsBlob(snapshot.orgId, snapshot.screenshotId)
            screenshot = screenshot.slice(0, screenshot.size, "image/png")
            const resizedScreenshot = await resizeFile(screenshot)
            const newSnapshot = {...rawSnapshot, orgId: snapshot.orgId, projectId: snapshot.projectId, configId: snapshot.configId, resizedScreenshot: resizedScreenshot}
            dispatch(configUpdate({id: snapshot.id, changes: newSnapshot}))
            return newSnapshot
        } catch(e) {
            return undefined
        }
    }
    public async fetchSnapshot(orgId:string, projectId:string, configId:string, snapshotId:string) : Promise<any> {
        return (await this.callApi("GET", `/orgs/${orgId}/projects/${projectId}/configs/${configId}/snapshots/${snapshotId}`)).json()
    }
    public async fetchSnapshotsAndRedux(dispatch:Dispatch<any>, config:Config) : Promise<Snapshot[]|undefined> {
        try {
            const rawSnapshots = await this.fetchSnapshots(config)
            const newSnapshots = await Promise.all(rawSnapshots.map(async (rawSnapshot) => {
                let screenshot = await this.downloadFileAsBlob(config.orgId, rawSnapshot.screenshotId)
                screenshot = screenshot.slice(0, screenshot.size, "image/png")
                const resizedScreenshot = await resizeFile(screenshot)
                return {...rawSnapshot, orgId: config.orgId, projectId: config.projectId, configId: config.id, resizedScreenshot: resizedScreenshot}
            }))
            dispatch(snapshotsSync({configId: config.id, snapshots: newSnapshots}))
            dispatch(treeOpenStatesAdd(newSnapshots.map(c => { return {id: c.id, openState: false}})))
            return newSnapshots
        } catch (e) {
            return undefined;
        }
    }
    public async fetchSnapshots(config: Config) : Promise<Snapshot[]> {
        return (await this.callApi("GET", `/orgs/${config.orgId}/projects/${config.projectId}/configs/${config.id}/snapshots/all`)).json()
    }
    public async editSnapshotAndRedux(dispatch: Dispatch<any>, snapshot: Snapshot) : Promise<Snapshot|undefined>{
        try {
            await this.editSnapshot(snapshot)
            return this.fetchSnapshotAndRedux(dispatch, snapshot)
        } catch(e) {
            return undefined
        }
    }
    public async editSnapshot(snapshot: Snapshot) : Promise<void> {
        return (await this.callApi("PUT", `/orgs/${snapshot.orgId}/projects/${snapshot.projectId}/configs/${snapshot.configId}/snapshots/${snapshot.id}`, JSON.stringify(snapshot))).json()
    }
    public async deleteSnapshotAndRedux(dispatch:Dispatch<any>, snapshot:Snapshot) : Promise<void|undefined> {
        try {
            const status = await this.deleteSnapshot(snapshot)
            dispatch(snapshotsRemove([snapshot.id]))
            return status
        } catch(e) {
            return undefined
        }
    }
    public async deleteSnapshot(snapshot:Snapshot) : Promise<void> {
        return (await this.callApi("DELETE", `/orgs/${snapshot.orgId}/projects/${snapshot.projectId}/configs/${snapshot.configId}/snapshots/${snapshot.id}`)).json()
    }

    // File upload/download
    public async uploadFile(orgId:string, binaryBody:any) : Promise<string> {
        return (await this.callApiWithBinary("POST", `/orgs/${orgId}/storages/`, binaryBody)).json()
    }
    public async multiUploadFile(orgId:string, binaryBody:Blob, progressCB:(progress:number)=>void, finalizeCB:()=>void) : Promise<string> {
        const multipartId = await (await this.callApi("POST", `/orgs/${orgId}/storages/multipart/`)).json()
        const chunkSizeLimit = 100 * 1024 * 1024
        const results = []
        let chunkCount = 0
        const chunkMax = 1 + binaryBody.size / chunkSizeLimit
        progressCB(0)
        for (let index=0,offset = 0; offset < binaryBody.size; index++,offset += chunkSizeLimit) {
            let chunkSize = chunkSizeLimit
            if (offset + chunkSizeLimit > binaryBody.size) {
                chunkSize = binaryBody.size
            }
            const chunk: Blob = binaryBody.slice(offset, offset + chunkSize)
            results.push((async () : Promise<Response> => {
                const ret = await this.callApiWithBinary("POST", `/orgs/${orgId}/storages/multipart/${multipartId}?action=upload&part=${index}`, chunk)
                chunkCount++
                progressCB((chunkCount * 100)/chunkMax )
                return ret
            })())
        }
        await Promise.all(results)
        finalizeCB()
        return await (await this.callApi("POST", `/orgs/${orgId}/storages/multipart/${multipartId}?action=finalize`)).json()
    }
    public async downloadFileAsBlob(orgId:string, fileId:string) : Promise<Blob> {
        return (await this.callApiWithBinary("GET", `/orgs/${orgId}/storages/${fileId}`)).blob()
    }
    public downloadFile(orgId:string, fileId:string, filename:string) : void {
        const path =  `/orgs/${orgId}/storages/${fileId}?filename=${filename}`
        const url = `${ApiClient.getApiServer()}${path}`
        window.open(url, '_blank')
    }
    public async deleteFile(orgId:string, fileId:string) : Promise<string> {
        return (await this.callApiWithBinary("DELETE", `/orgs/${orgId}/storages/${fileId}`)).json()
    }

    private async callApiWithBinary(method: string, path: string, binaryBody?: BodyInit): Promise<Response> {
        let headers: HeadersInit = {
        }
        let params: RequestInit = {
            method: method,
            headers: headers,
            credentials: "include",
        }
        if (binaryBody) {
            headers["Content-Type"] = "application/octet-stream"
            params.body = binaryBody
        }
        const response = await fetch(`${ApiClient.getApiServer()}${path}`, params)
        if (!response.ok) {
            throw new Error(`HTTP status = ${response.statusText}`)
        }
        return response
    }

    private async callApi(method: string, path: string, jsonBody?: string): Promise<Response> {
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
        return response
    }
}

export default ApiClient