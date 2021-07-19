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

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConfigsTest {
    @Test
    public void オブジェクトからJsonそしてJsonからオブジェクトへの変換ができること() throws Exception {
        Configs configs = Configs.create();
        configs.add(Config.create(Name.create("name1"), "a1", "b1", "c1", "d1"));
        configs.add(Config.create(Name.create("name2"), "a2", "b2", "c2", "d2"));
        configs.add(Config.create(Name.create("name3"), "a3", "b3", "c3", "d3"));
        //System.out.println(configs.toJson());
        Configs restored = Configs.createFromJson(configs.toJson());
        assertEquals(configs, restored);
    }

}
