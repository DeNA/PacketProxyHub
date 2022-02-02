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
import lombok.Getter;
import lombok.ToString;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Getter
@ToString
@EqualsAndHashCode
public class File
{
    static public File create() {
        return create(new ByteArrayInputStream("".getBytes()));
    }

    static public File create(InputStream data) {
        return create(Id.create(), data);
    }

    static public File create(Id id, InputStream data) {
        return new File(id, data);
    }

    private Id id;
    private InputStream data;

    private File(Id id, InputStream data) {
        this.id = id;
        this.data = data;
    }
}
