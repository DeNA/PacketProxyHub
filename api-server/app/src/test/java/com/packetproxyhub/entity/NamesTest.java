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

import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class NamesTest {

    @Test
    @Disabled
    public void toStringで文字列に変換できること() throws Exception {
        Names names = Names.create();
        names.add(ImmutableSet.of(Name.create(), Name.create(), Name.create()));
    }

    @Test
    public void equalsが正常に動作すること() throws Exception {
        Names names1 = Names.create();
        names1.add(ImmutableSet.of(Name.create("a"), Name.create("b"), Name.create("c")));
        Names names2 = Names.create();
        names2.add(ImmutableSet.of(Name.create("a"), Name.create("b"), Name.create("c")));
        assertEquals(names1, names2);
    }

    @Test
    public void 内容の違うnamesはequalsにならないこと() throws Exception {
        Names names1 = Names.create();
        names1.add(ImmutableSet.of(Name.create("a"), Name.create("b"), Name.create("c")));
        Names names2 = Names.create();
        names2.add(ImmutableSet.of(Name.create("a"), Name.create("p"), Name.create("c")));
        assertNotEquals(names1, names2);
    }

    @Test
    public void オブジェクトからJsonそしてJsonからオブジェクトへの変換ができること() throws Exception {
        Names names = Names.create();
        names.add(ImmutableSet.of(Name.create(), Name.create(), Name.create()));
        System.out.println(names.toJson());
        Names restored = Names.createFromJson(names.toJson());
        assertEquals(names, restored);
    }

}
