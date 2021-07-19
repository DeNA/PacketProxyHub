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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CryptTest {
    @Test
    public void testSmoke() throws Exception {
        Crypt crypt = new Crypt("1234567890123456".getBytes());

        String targetText = "Hello, world!";
        byte[] target = targetText.getBytes();

        byte[] encrypted = crypt.encrypt(target);
        byte[] decrypted = crypt.decrypt(encrypted);

        assertArrayEquals(target, decrypted);
    }

    @Test
    public void testDecryptBrokenData() throws Exception {
        Crypt crypt = new Crypt("1234567890123456".getBytes());

        String targetText = "Hello, world!";
        byte[] target = targetText.getBytes();

        byte[] encrypted = crypt.encrypt(target);
        encrypted[13] = 0x00;
        assertThrows(Exception.class, () -> crypt.decrypt(encrypted));
    }

    @Test
    public void testDecryptWithWrongKey() throws Exception {
        Crypt crypt1 = new Crypt("1234567890123456".getBytes());
        Crypt crypt2 = new Crypt("6543210987654321".getBytes());

        String targetText = "Hello, world!";
        byte[] target = targetText.getBytes();

        byte[] encrypted = crypt1.encrypt(target);
        assertThrows(Exception.class, () -> crypt2.decrypt(encrypted));
    }
}
