package com.geekbrains.common.seirialization.template;


import java.io.Serializable;

public abstract class AbstractMessage implements Serializable {
    public enum Type{REQUEST, RESPONSE, EXCEPTION}
    private Type type;

    public AbstractMessage(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    protected void  setType(Type type) {
        this.type = type;
    }
}
