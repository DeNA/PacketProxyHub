package com.packetproxyhub.repository.filesystem;

import com.packetproxyhub.entity.file.File;
import com.packetproxyhub.entity.Id;
import com.packetproxyhub.repository.filesystem.mock.MockFilesystem;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MockFilesystemTest
{
    static MockFilesystem filesystem;

    @BeforeAll
    public static void setup() throws Exception {
        filesystem = new MockFilesystem();
    }

    @Test
    public void ファイルを保存して読み出す() throws Exception {
        File file1 = File.create(IOUtils.toInputStream("contents of file1", StandardCharsets.UTF_8));
        filesystem.writeFile(file1);
        File file2 = filesystem.readFile(file1.getId());
        assertEquals("contents of file1", IOUtils.toString(file2.getData(), StandardCharsets.UTF_8));
    }

    @Test
    public void ファイルを削除できること() throws Exception {
        File file1 = File.create(IOUtils.toInputStream("contents of file1", StandardCharsets.UTF_8));
        filesystem.writeFile(file1);
        filesystem.removeFile(file1.getId());
        assertThrows(Exception.class, () -> filesystem.readFile(file1.getId()));
    }

    @Test
    public void 存在しないファイルを削除できないこと() throws Exception {
        assertThrows(Exception.class, () -> filesystem.removeFile(Id.create()));
    }

}