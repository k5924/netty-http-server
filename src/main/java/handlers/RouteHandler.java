package handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;

public final class RouteHandler extends SimpleChannelInboundHandler<FullHttpRequest> {


    @Override
    protected void channelRead0(final ChannelHandlerContext channelHandlerContext,
                                final FullHttpRequest fullHttpRequest) throws Exception {
        final SimpleChannelInboundHandler<FullHttpRequest> handler;
        if (fullHttpRequest.uri().equals("/")) {
            handler = new OkResponseHandler();
        } else {
            handler = new NotFoundHandler();
        }
        handler.channelRead(channelHandlerContext, fullHttpRequest.retain());
    }
}
