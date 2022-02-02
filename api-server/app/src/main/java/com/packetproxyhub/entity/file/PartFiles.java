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

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


@ToString
@EqualsAndHashCode
public class PartFiles
{
    static public PartFiles create() {
        return create(new HashMap<Integer, File>());
    }

    static public PartFiles create(Map<Integer, File> partFiles) {
        return new PartFiles(partFiles);
    }

    private Map<Integer, File> partFiles;

    private PartFiles(Map<Integer, File> partFiles) {
        this.partFiles = partFiles;
    }

    public void add(int partNumber, File file) {
        partFiles.put(partNumber, file);
    }

    public File[] toFiles() throws Exception {
        ArrayList<File> files = new ArrayList<File>();
        int max = partFiles.size();
        for (int i = 0; i < max; i++) {
            File file = partFiles.get(i);
            if (file == null) {
                throw new Exception();
            }
            files.add(file);
        }
        return files.toArray(new File[]{});
    }
}