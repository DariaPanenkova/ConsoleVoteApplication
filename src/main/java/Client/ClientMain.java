package Client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Scanner;

public class ClientMain {
    private final Scanner scanner = new Scanner(System.in);
    public Channel channel;
    public  final ClientHandler clientHandler = new ClientHandler();

    public static void main(String[] args) throws InterruptedException {
        new ClientMain().start();
    }

    public void start() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline()
                                    .addLast(new StringDecoder())
                                    .addLast(new StringEncoder())
                                    .addLast(clientHandler);
                        }
                    });

            channel = b.connect("localhost", 11111).sync().channel();
            try (scanner) {
                while (true) {
                    String input = scanner.nextLine().trim();
                    if (input.isEmpty()) continue;

                    String command = buildCommand(input);
                    if (command == null) {
                        System.out.println("Invalid command");
                        continue;
                    }

                    channel.writeAndFlush(command);
                    if (input.equalsIgnoreCase("exit")) break;
                }
            }
        } finally {
            group.shutdownGracefully();
        }
    }
    private  String login(String args) {
        if (!args.startsWith("-u=")) {
            System.out.println("Используйте: login -u=username");
            return null;
        }
        return "login|" + args.substring(3);
    }

    private  String view(String args) {
        if (args.isEmpty()) {
            return "view";
        } else if (args.startsWith("-t=")) {
            String argsStr = args.substring(3);
            if (argsStr.contains("-v=")) {
                String[] argsParts = argsStr.split(" -v=");
                return "view|" + argsParts[0] + "|" + argsParts[1];
            } else {
                return "view|" + argsStr;
            }
        } else {
            System.out.println("Используйте: view/view-t=<topic>/view -t=<topic> -v=<vote>");
            return null;
        }
    }

    private  String create(String args) {
        if (args.startsWith("topic ")) {
            return createTopic(args.substring(6));
        } else if (args.startsWith("vote ")) {
            return createVote(args.substring(5));
        } else {
            System.out.println("Используйте: create topic -n=<name>/create vote -t=<topic>");
            return null;
        }
    }

    private  String createTopic(String args) {
        if (args.startsWith("-n=")) {
            return "createTopic|" + args.substring(3);
        }else {
            System.out.println("Используйте: create topic -n=<name>/create vote -t=<topic>");
            return null;
        }
    }

    private String createVote(String args) {
        if (args.startsWith("-t=")) {
            StringBuilder result = new StringBuilder("createVote|" + args.substring(3));

            String voteName = "";

            while (voteName.isEmpty()){
                System.out.print("Введите название голосования: ");
                voteName = scanner.nextLine();
            }
            result.append("|" + voteName);

            String description = "";

            while (description.isEmpty()) {
                System.out.print("Введите описание: ");
                description = scanner.nextLine();
            }
            result.append("|" + description);

            int optionCount = 0;

            while (optionCount == 0) {
                System.out.print("Введите количество вариантов: ");
                optionCount = Integer.parseInt(scanner.nextLine());
            }

            result.append("|" + optionCount);

            for (int i = 0; i < optionCount; i++) {
                System.out.print("Вариант " + (i + 1) + ": ");
                result.append("|" +scanner.nextLine());
            }
            return result.toString();
        }else {
            System.out.println("Используйте: create topic -n=<name>/create vote -t=<topic>");
            return null;
        }
    }

    private String vote(String args) {
        if (args.contains("-t=")
            && args.contains("-v=")) {
            String request = view(args);
            channel.writeAndFlush(request);

            try {
                String response = clientHandler.waitForResponse();

                if (response.contains("OK|")) {
                    String option = "";

                    while (option.isEmpty()) {
                        System.out.print("Введите выбранный вариант ответа:");
                        option = scanner.nextLine();
                    }
                    String argsStr = args.substring(3);
                    String[] argsParts = argsStr.split(" -v=");
                    String resultRequst = "vote|" + argsParts[0] + "|" + argsParts[1] + "|" + option;

                    return resultRequst;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("Используйте: vote -t=<topic> -v=<vote>");
            return null;
        }

        return null;
    }

    private  String buildCommand(String input) {
        String[] parts = input.split(" ", 2);
        String command = parts[0];
        String args = parts.length > 1 ? parts[1] : "";

        switch (command) {
            case "login":
                return login(args);
            case "view":
                return view(args);
            case "create":
                return create(args);
            case "vote":
                return vote(args);
            case "delete":
                //TODO: перенести реализацию
                return null;
            case "exit":
                return "exit";
            default:
                return null;
        }

    }
}