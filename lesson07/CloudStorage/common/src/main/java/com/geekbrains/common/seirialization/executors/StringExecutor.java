package com.geekbrains.common.seirialization.executors;

import com.geekbrains.common.network.impl.Callback;
import com.geekbrains.common.network.impl.MessageExecutor;
import com.geekbrains.common.seirialization.executors.messages.StringMessage;
import com.geekbrains.common.seirialization.template.AbstractMessage;
import io.netty.channel.Channel;

public class StringExecutor implements MessageExecutor {
    private Callback<String> inputMessageCallback;

    public StringExecutor() {
        this(null);
    }

    public StringExecutor(Callback<String> inputMessageCallback) {
        this.inputMessageCallback = inputMessageCallback;
    }

    @Override
    public Class<? extends AbstractMessage> getInputMessageClass() {
        return StringMessage.class;
    }

    @Override
    public boolean isLongTimeOperation() {
        return false;
    }

    @Override
    public void execute(AbstractMessage abstractMessage, Channel channel) {
        StringMessage stringMessage = (StringMessage) abstractMessage;
        this.getInputMessageCallback().callback(stringMessage.getText());
    }

    public void send(String msg, Channel channel) {
        channel.writeAndFlush(new StringMessage(AbstractMessage.Type.REQUEST, msg));
    }

    private Callback<String> getInputMessageCallback() {
        return (this.inputMessageCallback == null) ? s->{} : this.inputMessageCallback;
    }
}
