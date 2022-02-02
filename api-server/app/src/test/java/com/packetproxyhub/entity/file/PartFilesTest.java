package com.packetproxyhub.entity.file;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PartFilesTest {

    @Test
    void toFilesのテスト() throws Exception {
        File file0 = File.create();
        File file1 = File.create();
        File file2 = File.create();
        PartFiles pf = PartFiles.create();
        pf.add(0, file0);
        pf.add(2, file2);
        pf.add(1, file1);
        File[] files = pf.toFiles();
        assertEquals(file0, files[0]);
        assertEquals(file1, files[1]);
        assertEquals(file2, files[2]);
    }

    @Test
    void 中間のPartFileが足りない状態でtoFilesすると例外が発生すること() throws Exception {
        File file0 = File.create();
        File file2 = File.create();
        PartFiles pf = PartFiles.create();
        pf.add(0, file0);
        pf.add(2, file2);
        assertThrows(Exception.class, pf::toFiles);
    }

}