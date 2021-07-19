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

class PacketProxyClient {

    static packetProxyUrl = "http://localhost:32349"

    private packetProxyAccessToken = ""

    public constructor(packetProxyAccessToken: string) {
        this.packetProxyAccessToken = packetProxyAccessToken
    }

    public async getConfig() : Promise<string|undefined> {
        try {
            const config = await this.callApi("GET", "/config")
            return JSON.stringify(config, null, 8)
        } catch (e) {
            return undefined
        }
    }

    public async postConfig(config: string) : Promise<string|undefined> {
        try {
            return await this.callApi("POST", "/config", config)
        } catch (e) {
            return undefined
        }
    }

private async callApi(method: string, path: string, jsonBody?: string): Promise<any> {
        let headers: { [key: string]: string } = {
            "Authorization": this.packetProxyAccessToken
        }
        let params: { method: string, headers: any, body?: string} = {
            method: method,
            headers: headers
        }
        if (jsonBody && jsonBody.length > 0) {
            headers["Content-Type"] = "application/json"
            params.body = jsonBody
        }
        const response = await fetch(`${PacketProxyClient.packetProxyUrl}${path}`, params)
        if (!response.ok) {
            throw new Error("HTTP status != 200")
        }
        return response.json()
    }

}

export default PacketProxyClient