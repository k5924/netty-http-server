package handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;

public final class RouteHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final String directory;

    public RouteHandler(final String directory) {
        this.directory = directory;
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext channelHandlerContext,
                                final FullHttpRequest fullHttpRequest) throws Exception {
        final SimpleChannelInboundHandler<FullHttpRequest> handler;
        if (fullHttpRequest.method().equals(HttpMethod.GET)) {
            if (fullHttpRequest.uri().equals("/")) {
                handler = new OkResponseHandler();
            } else if (fullHttpRequest.uri().contains("/echo")) {
                handler = new EchoHandler();
            } else if (fullHttpRequest.uri().contains("/user-agent")) {
                handler = new UserAgentHandler();
            } else if (fullHttpRequest.uri().contains("/files")) {
                handler = new FileReadHandler(directory);
            } else {
                handler = new NotFoundHandler();
            }
        } else if (fullHttpRequest.method().equals(HttpMethod.POST)) {
            if (fullHttpRequest.uri().contains("/files")) {
                handler = new FileWriteHandler(directory);
            } else {
                handler = new NotFoundHandler();
            }
        } else {
            handler = new NotFoundHandler();
        }
        handler.channelRead(channelHandlerContext, fullHttpRequest.retain());
    }
}
