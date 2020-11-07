package com.geekbrains.common.seirialization.executors.messages;


import com.geekbrains.common.seirialization.template.AbstractMessage;

public class StringMessage extends AbstractMessage {
    private String text;

    public StringMessage(AbstractMessage.Type type, String text) {
        super(type);
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
