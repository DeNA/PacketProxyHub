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
package com.packetproxyhub.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectTest {
    private Account account1;
    private Account account2;
    private Account account3;
    private Project project;

    @BeforeEach
    public void setup() {
        project = Project.create("a", "b", "c");
    }

    @Test
    public void オブジェクトからJsonそしてJsonからオブジェクトへの変換ができること() throws Exception {
        System.out.println(project.toJson());
        Project restored = Project.createFromJson(project.toJson());
        assertEquals(project, restored);
    }

}
