package Server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ServerHandler extends SimpleChannelInboundHandler<String> {
    private final VotingStorage storage;

    public ServerHandler(VotingStorage storage) {
        this.storage = storage;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        String response = storage.handleCommand(msg);
        ctx.writeAndFlush(response);

        if (msg.startsWith("exit|")) {
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
