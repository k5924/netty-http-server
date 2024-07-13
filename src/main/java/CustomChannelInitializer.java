import handlers.OkResponseHandler;
import handlers.RouteHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

public final class CustomChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final String directory;

    public CustomChannelInitializer(final String directory) {
        this.directory = directory;
    }

    @Override
    protected void initChannel(final SocketChannel socketChannel) throws Exception {
        final var pipeline = socketChannel.pipeline();
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new HttpContentCompressor());
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new RouteHandler(directory));
    }
}
