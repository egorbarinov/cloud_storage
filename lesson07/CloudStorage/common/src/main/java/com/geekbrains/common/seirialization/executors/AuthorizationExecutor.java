package com.geekbrains.common.seirialization.executors;

import com.geekbrains.common.network.impl.Callback;
import com.geekbrains.common.seirialization.executors.impl.AuthorisationMethod;
import com.geekbrains.common.seirialization.executors.messages.AuthorizationMessage;
import com.geekbrains.common.seirialization.template.AbstractMessage;
import com.geekbrains.common.seirialization.template.AbstractMessageExecutor;
import io.netty.channel.Channel;

public class AuthorizationExecutor extends AbstractMessageExecutor<AuthorizationMessage> {
    private final Callback<Boolean> authorizationResponse;
    private final AuthorisationMethod authorisationMethod;

    public AuthorizationExecutor(Callback<Boolean> authorizationResponse, AuthorisationMethod authorisationMethod) {
        this.authorizationResponse = authorizationResponse;
        this.authorisationMethod = authorisationMethod;
    }

    @Override
    public final Class<? extends AbstractMessage> getInputMessageClass() {
        return AuthorizationMessage.class;
    }

    @Override
    public final boolean isLongTimeOperation() {
        return false;
    }

    @Override
    protected final void executeRequest(AuthorizationMessage message, Channel channel) {
        channel.writeAndFlush(new AuthorizationMessage(
                AbstractMessage.Type.RESPONSE,
                this.getAuthorisationMethod().check(message.getLogin(), message.getPassword(), channel)
        ));
    }

    @Override
    protected final void executeResponse(AuthorizationMessage message, Channel channel) {
        this.getAuthorizationResponse().callback(message.getChecked());
    }

    @Override
    protected final void executeException(AuthorizationMessage message, Channel channel) {
    }

    private Callback<Boolean> getAuthorizationResponse() {
        return (this.authorizationResponse == null) ? (b)->{} : this.authorizationResponse;
    }

    private AuthorisationMethod getAuthorisationMethod() {
        return (this.authorisationMethod == null) ? (l, p, ch)-> true : this.authorisationMethod;
    }

    public void sendLoginAndPass(String login, String pass, Channel channel) {
        channel.writeAndFlush(new AuthorizationMessage(AbstractMessage.Type.REQUEST, login, pass));
    }
}
