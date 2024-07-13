package handlers;

import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;

import java.io.File;
import java.io.RandomAccessFile;

public final class FileReadHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final String directory;

    public FileReadHandler(final String directory) {
        this.directory = directory;
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext channelHandlerContext,
                                final FullHttpRequest fullHttpRequest) throws Exception {
        final var uriArr = fullHttpRequest.uri().split("/");
        final DefaultFullHttpResponse response;
        if (uriArr.length > 2) {
            final var fileName = directory + uriArr[2];
            final var file = new File(fileName);
            if (file.exists()) {
                response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                final var raf = new RandomAccessFile(file, "r");
                final var fileLength = raf.length();
                response.headers()
                        .add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_OCTET_STREAM)
                        .add(HttpHeaderNames.CONTENT_LENGTH, fileLength);
                final var arr = new byte[(int) fileLength];
                raf.readFully(arr);
                raf.close();
                final var content = Unpooled.wrappedBuffer(arr);
                response.content().writeBytes(content);
            } else {
                response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
            }
        } else {
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
        }
        channelHandlerContext
                .writeAndFlush(response)
                .addListener(ChannelFutureListener.CLOSE);
    }
}
