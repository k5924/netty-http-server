package handlers;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

public final class OkResponseHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(final ChannelHandlerContext channelHandlerContext,
                                final FullHttpRequest fullHttpRequest) throws Exception {
        final var response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        channelHandlerContext
                .writeAndFlush(response)
                .addListener(ChannelFutureListener.CLOSE);
    }
}
