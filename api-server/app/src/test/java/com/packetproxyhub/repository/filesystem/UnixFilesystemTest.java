package com.packetproxyhub.repository.filesystem;

import com.packetproxyhub.entity.file.File;
import com.packetproxyhub.entity.Id;
import com.packetproxyhub.repository.filesystem.unix.UnixFilesystem;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UnixFilesystemTest {

    static UnixFilesystem filesystem;
    static Path dataDirPath;

    @BeforeAll
    public static void setup() throws Exception {
        dataDirPath = FileSystems.getDefault().getPath("/tmp/" + UUID.randomUUID().toString());
        filesystem = new UnixFilesystem(dataDirPath.toString());
    }

    @AfterAll
    public static void tearDown() throws Exception {
        filesystem.deleteAllData();
    }

    @Test
    public void ファイルを保存する() throws Exception {
        File file = File.create(IOUtils.toInputStream("aaa", StandardCharsets.UTF_8));
        filesystem.writeFile(file);
        java.io.File javaFile = dataDirPath.resolve(file.getId().toString()).toFile();
        assertEquals(true, javaFile.exists());
    }

    @Test
    public void ファイルを読み出す() throws Exception {
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

    @Test
    public void 複数のファイルをマージできること() throws Exception {
        File file1 = File.create(IOUtils.toInputStream("New", StandardCharsets.UTF_8));
        File file2 = File.create(IOUtils.toInputStream("Apple", StandardCharsets.UTF_8));
        filesystem.writeFile(file1);
        filesystem.writeFile(file2);
        File mergedFile = filesystem.mergeFile(Id.create(), new File[]{file1,file2});
        assertEquals("NewApple", IOUtils.toString(mergedFile.getData(), StandardCharsets.UTF_8));
    }

    @Test
    public void 巨大ファイルでファイルをマージできること() throws Exception {
        File bigFile1 = filesystem.createBigFileForDebug();
        File bigFile2 = filesystem.createBigFileForDebug();
        File bigFile3 = filesystem.createBigFileForDebug();
        File bigFile4 = filesystem.createBigFileForDebug();
        File bigFile5 = filesystem.createBigFileForDebug();
        File mergedFile = filesystem.mergeFile(Id.create(), new File[]{bigFile1,bigFile2,bigFile3,bigFile4,bigFile5});
        assertTrue(mergedFile.getId() != null);
    }

}