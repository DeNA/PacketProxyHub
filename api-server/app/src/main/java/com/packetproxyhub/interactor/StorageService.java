package com.packetproxyhub.interactor;

import com.packetproxyhub.entity.AccessChecker.AccessChecker;
import com.packetproxyhub.entity.file.File;
import com.packetproxyhub.entity.Id;
import com.packetproxyhub.entity.file.MultiPartFile;

import javax.inject.Inject;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class StorageService implements IStorageService {
    @Inject
    private IFilesystem filesystem;
    @Inject
    private IRepository repository;

    private Map<Id, MultiPartFile> multiPartFiles = new HashMap<>();

    @Override
    public Id generateId(Id accountId, Id orgId) throws Exception {
        if (!AccessChecker.create(accountId, repository).isOrgWritable(orgId)) {
            throw new IllegalAccessException();
        }
        Id multiPartId = Id.create();
        multiPartFiles.put(multiPartId, MultiPartFile.create(multiPartId));
        return multiPartId;
    }

    @Override
    public void putPart(Id accountId, Id orgId, Id multiPartId, int partNumber, InputStream inputStream) throws Exception {
        if (!AccessChecker.create(accountId, repository).isOrgWritable(orgId)) {
            throw new IllegalAccessException();
        }
        MultiPartFile multiPartFile = multiPartFiles.get(multiPartId);
        File file = File.create(inputStream);
        filesystem.writeFile(file);
        multiPartFile.add(partNumber, file);
    }

    @Override
    public Id mergeParts(Id accountId, Id orgId, Id multiPartId) throws Exception {
        if (!AccessChecker.create(accountId, repository).isOrgWritable(orgId)) {
            throw new IllegalAccessException();
        }
        MultiPartFile multiPartFile = multiPartFiles.get(multiPartId);
        filesystem.mergeFile(multiPartId, multiPartFile.toFiles());
        return multiPartId;
    }

    @Override
    public Id put(Id accountId, Id orgId, InputStream inputStream) throws Exception {
        if (!AccessChecker.create(accountId, repository).isOrgWritable(orgId)) {
            throw new IllegalAccessException();
        }
        File file = File.create(inputStream);
        filesystem.writeFile(file);
        return file.getId();
    }

    @Override
    public InputStream get(Id accountId, Id orgId, Id fileId) throws Exception {
        if (!AccessChecker.create(accountId, repository).isOrgReadable(orgId)) {
            throw new IllegalAccessException();
        }
        File file = filesystem.readFile(fileId);
        return file.getData();
    }

    @Override
    public void remove(Id accountId, Id orgId, Id fileId) throws Exception {
        if (!AccessChecker.create(accountId, repository).isOrgWritable(orgId)) {
            throw new IllegalAccessException();
        }
        filesystem.removeFile(fileId);
    }
}
