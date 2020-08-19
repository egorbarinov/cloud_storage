package com.geekbrains.common.network.impl;

import com.geekbrains.common.seirialization.template.AbstractMessage;
import io.netty.channel.Channel;

public interface MessageExecutor {
    Class<? extends AbstractMessage> getInputMessageClass();
    boolean isLongTimeOperation();
    void execute(AbstractMessage abstractMessage, Channel channel);
}
