package Client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientHandler extends SimpleChannelInboundHandler<String> {
    private final Object lock = new Object();
    private String lastResponse;

    public String waitForResponse() throws InterruptedException {
        synchronized (lock) {
            lock.wait(5000);
            return lastResponse;
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        synchronized (lock) {
            this.lastResponse = msg;
            lock.notifyAll();
        }

        if (msg.startsWith("OK|")) {
            System.out.println(msg.substring(3));
        } else if (msg.startsWith("ERROR|")) {
            System.err.println("Error: " + msg.substring(6));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}