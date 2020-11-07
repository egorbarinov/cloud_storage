package com.geekbrains.common.network.impl;

import io.netty.channel.ChannelHandler;

public interface IHandlerFactory {
    ChannelHandler[] getHandlers();
}
