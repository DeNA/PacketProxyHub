package com.packetproxyhub.repository.filesystem.mock;

import com.packetproxyhub.entity.file.File;
import com.packetproxyhub.entity.Id;
import com.packetproxyhub.interactor.IFilesystem;

import java.util.HashMap;
import java.util.Map;

public class MockFilesystem implements IFilesystem {
    Map<Id, File> files = new HashMap<>();

    @Override
    public File readFile(Id fileId) throws Exception {
        if (!files.containsKey(fileId)) {
            throw new Exception("[Error] file not found");
        }
        return files.get(fileId);
    }

    @Override
    public void writeFile(File file) {
        files.put(file.getId(), file);
    }

    @Override
    public void removeFile(Id fileId) throws Exception {
        if (!files.containsKey(fileId)) {
            throw new Exception("[Error] file not found");
        }
        files.remove(fileId);
    }

    @Override
    public File mergeFile(Id newFileId, File[] files) throws Exception {
        /* not implemented yet */
        return null;
    }
}
