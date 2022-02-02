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

public interface IBinaryService {
    Ids listBinaries(Id accountId, Id orgId, Id projectId, Id configId) throws Exception;
    Binaries getBinaries(Id accountId, Id orgId, Id projectId, Id configId) throws Exception;
    Id createBinary(Id accountId, Id orgId, Id projectId, Id configId, Binary binary) throws Exception;
    Binary getBinary(Id accountId, Id orgId, Id projectId, Id configId, Id binaryId) throws Exception;
    void updateBinary(Id accountId, Id orgId, Id projectId, Id configId, Id binaryId, Binary binary) throws Exception;
    void removeBinary(Id accountId, Id orgId, Id projectId, Id configId, Id binaryId) throws Exception;
}
