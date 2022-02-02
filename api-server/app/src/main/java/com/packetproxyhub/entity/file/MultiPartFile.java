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
package com.packetproxyhub.entity.file;

import com.packetproxyhub.entity.Id;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

@ToString
@EqualsAndHashCode
public class MultiPartFile
{
    static public MultiPartFile create() {
        return create(Id.create(), PartFiles.create());
    }

    static public MultiPartFile create(Id id) {
        return create(id, PartFiles.create());
    }

    static public MultiPartFile create(PartFiles partFiles) {
        return create(Id.create(), partFiles);
    }

    static public MultiPartFile create(Id id, PartFiles partFiles) {
        return new MultiPartFile(id, partFiles);
    }

    private Id id;
    private PartFiles partFiles;

    private MultiPartFile(Id id, PartFiles partFiles) {
        this.id = id;
        this.partFiles = partFiles;
    }

    public void add(int partNumber, File file) {
        this.partFiles.add(partNumber, file);
    }

    public File[] toFiles() throws Exception {
        return this.partFiles.toFiles();
    }
}
