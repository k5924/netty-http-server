package handlers;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;

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
            final var fileName = directory + "/" + uriArr[2];
            final var file = new File(fileName);
            if (file.exists()) {
                response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
                final var raf = new RandomAccessFile(file, "r");
                final var fileLength = raf.length();
                HttpUtil.setContentLength(response, fileLength);
                response.headers()
                        .add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_OCTET_STREAM)
                        .add(HttpHeaderNames.CONTENT_LENGTH, fileLength);
                channelHandlerContext.write(response);
                final var region = new DefaultFileRegion(raf.getChannel(), 0, fileLength);
                channelHandlerContext
                        .write(region);

            } else {
                response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
                channelHandlerContext
                        .writeAndFlush(response)
                        .addListener(ChannelFutureListener.CLOSE);
            }
        } else {
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
            channelHandlerContext
                    .writeAndFlush(response)
                    .addListener(ChannelFutureListener.CLOSE);
        }

    }
}