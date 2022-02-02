package com.packetproxyhub.repository.filesystem.unix;

import com.packetproxyhub.application.App;
import com.packetproxyhub.entity.file.File;
import com.packetproxyhub.entity.Id;
import com.packetproxyhub.interactor.IFilesystem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class UnixFilesystem implements IFilesystem {

    private Path dataDirPath;

    public UnixFilesystem() {
        this(App.settings.getDataDirPath());
    }

    public UnixFilesystem(String dataDirPathStr) {
        dataDirPath = FileSystems.getDefault().getPath(dataDirPathStr);
        if (!dataDirPath.toFile().exists()) {
            dataDirPath.toFile().mkdirs();
        }
    }

    public void deleteAllData() throws Exception {
        FileUtils.deleteDirectory(dataDirPath.toFile());
    }

    public File createBigFileForDebug() throws Exception {
        Id bigfileId = Id.create();
        Path bigfilePath = dataDirPath.resolve(bigfileId.toString());
        RandomAccessFile f = new RandomAccessFile(bigfilePath.toString(), "rw");
        f.setLength(1024 * 1024 * 1024);
        f.close();
        return File.create(bigfileId, new FileInputStream(bigfilePath.toFile()));
    }

    @Override
    public File readFile(Id fileId) throws Exception {
        java.io.File javaFile = dataDirPath.resolve(fileId.toString()).toFile();
        if (!javaFile.exists()) {
            throw new Exception("[Error] file not found");
        }
        return File.create(fileId, new FileInputStream(javaFile));
    }

    @Override
    public void writeFile(File file) throws Exception {
        java.io.File javaFile = dataDirPath.resolve(file.getId().toString()).toFile();
        try (OutputStream outputStream = new FileOutputStream(javaFile)) {
            IOUtils.copy(file.getData(), outputStream);
        }
    }

    @Override
    public void removeFile(Id fileId) throws Exception {
        java.io.File javaFile = dataDirPath.resolve(fileId.toString()).toFile();
        if (!javaFile.exists()) {
            throw new Exception("[Error] file not found");
        }
        javaFile.delete();
    }

    @Override
    public File mergeFile(Id newFileId, File[] files) throws Exception {
        java.io.File newJavaFile = dataDirPath.resolve(newFileId.toString()).toFile();
        try (OutputStream newFileOutputStream = new FileOutputStream(newJavaFile)) {
            for (File file : files) {
                java.io.File eachJavaFile = dataDirPath.resolve(file.getId().toString()).toFile();
                if (!eachJavaFile.exists()) {
                    throw new Exception("[Error] file not found");
                }
                IOUtils.copy(new FileInputStream(eachJavaFile), newFileOutputStream);
                eachJavaFile.delete();
            }
        }
        return File.create(newFileId, new FileInputStream(newJavaFile));
    }
}
