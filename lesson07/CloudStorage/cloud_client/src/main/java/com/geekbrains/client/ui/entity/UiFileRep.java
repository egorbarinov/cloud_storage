package com.geekbrains.client.ui.entity;

import com.geekbrains.common.seirialization.executors.messages.entitys.FileRep;
import com.geekbrains.common.utils.FileUtil;

import java.io.File;

public class UiFileRep {
    private String name;
    private String type;
    private String size;

    public UiFileRep(String name, boolean isDir, long length) {
        this.setName(name);
        this.setType(isDir);
        this.setSize(length);
    }

    public UiFileRep(File file) {
        this.setName(file.getName());
        this.setType(file.isDirectory());
        this.setSize(file.length());
    }

    public UiFileRep(FileRep file) {
        this.setName(file.getName());
        this.setType(file.getDir());
        this.setSize(file.getSize());
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getSize() {
        return size;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(Boolean isDir) {
        this.type = (isDir) ? "папка" : "файл";
    }

    public void setSize(Long size) {
        this.size = (this.isDir()) ? "0 B" : FileUtil.getHumanFileLength(size);
    }

    public boolean isDir() {
        return !this.type.endsWith("файл");
    }
}
