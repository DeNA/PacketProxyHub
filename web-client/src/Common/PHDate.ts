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


class PHDate {
    private unixtime : number

    public constructor(unixtime: number | undefined) {
        this.unixtime = unixtime !== undefined ? unixtime : 0
    }
    public toString() : string {
        const d = new Date(this.unixtime * 1000)
        const year  = d.getFullYear()
        const month = d.getMonth() + 1
        const day  = d.getDate()
        const hour = ( d.getHours()   < 10 ) ? '0' + d.getHours()   : d.getHours()
        const min  = ( d.getMinutes() < 10 ) ? '0' + d.getMinutes() : d.getMinutes()
        const sec   = ( d.getSeconds() < 10 ) ? '0' + d.getSeconds() : d.getSeconds()
        return `${year}-${month}-${day} ${hour}:${min}:${sec}`
    }
}

export default PHDate