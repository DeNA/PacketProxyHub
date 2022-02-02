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
package com.packetproxyhub.interactor;

import com.packetproxyhub.entity.*;

import java.io.InputStream;

public interface IStorageService {
    Id generateId(Id accountId, Id orgId) throws Exception;
    void putPart(Id accountId, Id orgId, Id fileId, int partNumber, InputStream data) throws Exception;
    Id mergeParts(Id accountId, Id orgId, Id multiPartId) throws Exception;
    Id put(Id accountId, Id orgId, InputStream data) throws Exception;
    InputStream get(Id accountId, Id orgId, Id fileId) throws Exception;
    void remove(Id accountId, Id orgId, Id fileId) throws Exception;
}
