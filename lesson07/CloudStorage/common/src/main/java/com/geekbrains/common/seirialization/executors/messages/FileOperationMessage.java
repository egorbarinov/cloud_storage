package com.geekbrains.common.seirialization.executors.messages;


import com.geekbrains.common.seirialization.template.AbstractMessage;

public class FileOperationMessage extends AbstractMessage {
    public enum OType{DELETE, COPY, MOVE}
    private final String fileName;
    private final OType oType;
    private final String newFileName;

    public FileOperationMessage(AbstractMessage.Type type, OType type1, String fileName, String newFileName) {
        super(type);
        this.fileName = fileName;
        this.oType = type1;
        this.newFileName = newFileName;
    }

    public OType getOType() {
        return this.oType;
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getNewFileName() {
        return this.newFileName;
    }
}
