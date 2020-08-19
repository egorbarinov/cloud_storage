package com.geekbrains.common.seirialization;

import com.geekbrains.common.network.impl.Callback;
import com.geekbrains.common.network.impl.IHandlerFactory;
import com.geekbrains.common.network.impl.MessageExecutor;
import com.geekbrains.common.seirialization.handler.MessageHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.util.Objects;

public class MyHandlerFactory implements IHandlerFactory {
    private final Callback<Channel> ifConnect;
    private final Callback<Channel> ifDisconnect;
    private final MessageExecutor[] messageExecutors;

    public MyHandlerFactory(Callback<Channel> ifConnect, Callback<Channel> ifDisconnect, MessageExecutor... messageExecutors) {
        this.ifConnect = ifConnect;
        this.ifDisconnect = ifDisconnect;
        this.messageExecutors = messageExecutors;
    }

    @Override
    public ChannelHandler[] getHandlers() {
        Objects.requireNonNull(this.messageExecutors);

        return new ChannelHandler[]{
                new ObjectEncoder(),
                new ObjectDecoder(1024 * 1024 * 2, ClassResolvers.cacheDisabled(null)),
                new MessageHandler(this.ifConnect, this.ifDisconnect, messageExecutors)
        };
    }
}
