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

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProjectsTest {
    private Project projectA;
    private Project projectB;
    private Projects projects;

    @BeforeEach
    public void setup() {
        projectA = Project.create("a", "b", "c");
        projectB = Project.create("a", "b", "c");

        projects = new Projects();
        projects.add(projectA);
        projects.add(projectB);
    }

    @Test
    public void オブジェクトからJsonそしてJsonからオブジェクトへの変換ができること() throws Exception {
        System.out.println(projects.toJson());
        Projects restored = Projects.createFromJson(projects.toJson());
        assertEquals(projects, restored);
    }

}
