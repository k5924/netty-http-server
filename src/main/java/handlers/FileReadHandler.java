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
                        .add("Content-Type", "application/octet-stream")
                        .add(HttpHeaderNames.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"");
                channelHandlerContext.write(response);
                final var sendFileFuture = channelHandlerContext.write(new ChunkedFile(raf, 0, fileLength, 8192), channelHandlerContext.newProgressivePromise());
                final var lastContentFuture = channelHandlerContext.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
                sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
                    @Override
                    public void operationProgressed(final ChannelProgressiveFuture channelProgressiveFuture,
                                                    final long progress,
                                                    final long total) throws Exception {
                        if (total < 0) {
                            System.err.println(channelProgressiveFuture.channel() + " Transfer progress: " + progress);
                        } else {
                            System.err.println(channelProgressiveFuture.channel() + " Transfer progress: " + progress + "/" + total);
                        }
                    }

                    @Override
                    public void operationComplete(final ChannelProgressiveFuture channelProgressiveFuture) throws Exception {
                        System.err.println(channelProgressiveFuture.channel() + " Transfer complete.");
                    }
                });
                lastContentFuture.addListener(ChannelFutureListener.CLOSE);
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
