package Server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import java.util.Scanner;

public class ServerMain {
    private static volatile boolean isRunning = true;
    private static VotingStorage storage;

    public static void main(String[] args) throws InterruptedException {
        storage = new VotingStorage();

        // Запуск Netty сервера в отдельном потоке
        new Thread(() -> {
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workerGroup = new NioEventLoopGroup();

            try {
                ServerBootstrap b = new ServerBootstrap();
                b.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) {
                                ch.pipeline()
                                        .addLast(new StringDecoder())
                                        .addLast(new StringEncoder())
                                        .addLast(new ServerHandler(storage));
                            }
                        });

                ChannelFuture f = b.bind(11111).sync();
                System.out.println("Сервер запущен на порту 11111");
                f.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            }
        }).start();

        // Обработка консольных команд сервера
        handleConsoleCommands();
    }

    private static void handleConsoleCommands() {
        Scanner scanner = new Scanner(System.in);
        while (isRunning) {
            System.out.print("server> ");
            String input = scanner.nextLine().trim();

            if (input.startsWith("load ")) {
                String filename = input.substring(5);
                System.out.println(storage.handleCommand("load|" + filename));
            }
            else if (input.startsWith("save ")) {
                String filename = input.substring(5);
                System.out.println(storage.handleCommand("save|" + filename));
            }
            else if (input.equals("exit")) {
                isRunning = false;
                System.out.println("Завершение работы сервера...");
                System.exit(0);
            }
            else {
                System.out.println("Доступные команды сервера:");
                System.out.println("load <filename> - загрузить данные");
                System.out.println("save <filename> - сохранить данные");
                System.out.println("exit - завершить работу");
            }
        }
        scanner.close();
    }
}