package handlers;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.io.File;
import java.io.RandomAccessFile;

public final class FileWriteHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final String directory;

    public FileWriteHandler(final String directory) {
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
            file.createNewFile();
            final var raf = new RandomAccessFile(file, "rw");
            raf.setLength(0);
            final var content = fullHttpRequest.content();
            raf.write(content.array());
            raf.close();
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CREATED);
        } else {
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
        }
        channelHandlerContext
                .writeAndFlush(response)
                .addListener(ChannelFutureListener.CLOSE);
    }
}
