package com.geekbrains.common.seirialization.template;

import com.geekbrains.common.network.impl.MessageExecutor;
import io.netty.channel.Channel;

public abstract class AbstractMessageExecutor<T extends AbstractMessage> implements MessageExecutor {

    @Override
    @SuppressWarnings("unchecked")
    public final void execute(AbstractMessage abstractMessage, Channel channel) {
        if (abstractMessage.getType().equals(AbstractMessage.Type.RESPONSE)) {
            this.executeResponse((T)abstractMessage, channel);
        } else if (abstractMessage.getType().equals(AbstractMessage.Type.REQUEST)) {
            this.executeRequest((T)abstractMessage, channel);
        } else if (abstractMessage.getType().equals(AbstractMessage.Type.EXCEPTION)) {
            this.executeException((T)abstractMessage, channel);
        }
    }

    protected abstract void executeResponse(T message, Channel channel);

    protected abstract void executeRequest(T message, Channel channel);

    protected abstract void executeException(T message, Channel channel);
}
