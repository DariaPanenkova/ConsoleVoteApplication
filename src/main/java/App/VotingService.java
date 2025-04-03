package App;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class VotingService {
    private final Map<String, Topic> topics = new HashMap<>();
    private String currentUser;
    private final Scanner scanner = new Scanner(System.in);
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final String dataFile = "resources/VoteAppData.json";


    public void start() {
        loadData();
        System.out.println("Система голосований запущена. Введите 'help' для списка команд.");

        try (scanner) {
            while (true) {
                System.out.print("---> ");
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) continue;

                try {
                    if (process(input)) {
                        break; // Выход если команда exit
                    }
                } catch (Exception e) {
                    System.out.println("Ошибка: " + e.getMessage());
                }
            }
        }
    }

    private void saveData() {
        try (Writer writer = new FileWriter(dataFile)) {
            gson.toJson(topics, writer);
            System.out.println("Данные сохранены");
        } catch (IOException e) {
            System.out.println("Ошибка сохранения данных: " + e.getMessage());
        }
    }

    private void loadData() {
        if (!Files.exists(Path.of(dataFile))) {
            return;
        }

        try (Reader reader = new FileReader(dataFile)) {
            Type type = new TypeToken<HashMap<String, Topic>>() {
            }.getType();
            Map<String, Topic> loaded = gson.fromJson(reader, type);

            if (loaded != null) {
                topics.clear();
                topics.putAll(loaded);
            }
        } catch (IOException e) {
            System.out.println("Ошибка загрузки данных: " + e.getMessage());
        }
    }

    private void checkIsLogin() {
        if (currentUser == null) {
            throw new IllegalStateException("Сначала выполните вход (login)");
        }
    }

    private void createTopic(String args) {
        if (!args.startsWith("-n=")) {
            throw new IllegalArgumentException("Используйте: create topic -n=name");
        }
        String topicName = args.substring(3);

        if (topics.containsKey(topicName)) {
            throw new IllegalArgumentException("Раздел уже существует");
        }

        Topic topic = new Topic.TopicBuilder().topicName(topicName).build();
        topics.put(topicName, topic);
        System.out.println("Раздел создан: " + topicName);
    }

    private void createVote(String args) {
        if (!args.startsWith("-t=")) {
            throw new IllegalArgumentException("Используйте: create vote -t=topic");
        }

        String topicName = args.substring(3);

        Topic topic = topics.get(topicName);

        if (topic == null) {
            throw new IllegalArgumentException("Раздел не найден");
        }

        System.out.print("Введите название голосования: ");
        String voteName = scanner.nextLine();

        if (topic.hasVote(voteName)) {
            throw new IllegalArgumentException("Опрос с таким именем уже существует");
        }

        System.out.print("Введите описание: ");
        String description = scanner.nextLine();

        System.out.print("Введите количество вариантов: ");
        int optionCount = Integer.parseInt(scanner.nextLine());

        Vote.VoteBuilder voteBuilder = new Vote.VoteBuilder()
                .voteName(voteName)
                .description(description)
                .loginCreator(currentUser)
                .maxOptions(optionCount);

        for (int i = 0; i < optionCount; i++) {
            System.out.print("Вариант " + (i + 1) + ": ");
            voteBuilder.addOption(scanner.nextLine());
        }

        Vote vote = voteBuilder.build();

        topic.addVote(vote);
        System.out.println("Опрос " + voteName + " добавлен");

    }

    private void login(String args) {
        if (!args.startsWith("-u=")) {
            throw new IllegalArgumentException("Используйте: login -u=username");
        }
        currentUser = args.substring(3);
        System.out.println("Вы вошли как: " + currentUser);
    }

    private void create(String args) {
        checkIsLogin();

        if (args.startsWith("topic ")) {
            createTopic(args.substring(6));
        } else if (args.startsWith("vote ")) {
            createVote(args.substring(5));
        } else {
            throw new IllegalArgumentException("Используйте: create topic -n=<name>/create vote -t=<topic>");
        }
    }

    private void view(String args) {
        if (args.isEmpty()) {
            listTopics();
        } else if (args.startsWith("-t=")) {
            String argsStr = args.substring(3);
            if (argsStr.contains("-v=")) {
                String[] argsParts = argsStr.split(" -v=");
                viewVoteResult(argsParts[0], argsParts[1]);
            } else {
                listVotesInTopic(argsStr);
            }
        } else {
            throw new IllegalArgumentException("Используйте: view/view-t=<topic>/view -t=<topic> -v=<vote>");
        }
    }

    private void listTopics() {
        if (topics.isEmpty()) {
            System.out.println("Разделы не созданы");
            return;
        }
        topics.forEach((topicName, topic) -> {
            System.out.printf("<%s(votes in topic=%d)>%n", topicName, topic.getVotesCount());
        });
    }

    private void listVotesInTopic(String topicName) {
        Topic topic = topics.get(topicName);
        if (topic == null) {
            throw new IllegalArgumentException("Тема не найдена");
        }

        topic.getSetVotesName().forEach(System.out::println);
    }

    private void viewVoteResult(String topicName, String voteName) {
        Topic topic = topics.get(topicName);
        if (topic == null) {
            throw new IllegalArgumentException("Тема не найдена");
        }

        Vote vote = topic.getVote(voteName);
        System.out.println(vote.getVoteName());
        vote.getOptionsMap().forEach((option, voteCount) -> {
            System.out.printf("%s:%d %n", option, voteCount);
        });
    }

    private void help() {
        System.out.println("  login -u=username - Войти под именем");
        System.out.println("  create topic -n=name - Создать раздел");
        System.out.println("  create vote -t=topic - Создать голосование");
        System.out.println("  view - Список разделов");
        System.out.println("  view -t=topic - Список голосований в разделе");
        System.out.println("  view -t=topic -v=vote - Просмотр голосования");
        System.out.println("  vote -t=topic -v=vote - Проголосовать");
        System.out.println("  delete -t=topic -v=vote - Удалить голосование");
        System.out.println("  save - Сохранить данные");
        System.out.println("  exit - Выход");
    }

    private void delete(String args) {
        checkIsLogin();

        if (args.startsWith("-t=")
                && args.contains("-v=")) {
            String argsStr = args.substring(3);
            String[] argsParts = argsStr.split(" -v=");
            Topic topic = topics.get(argsParts[0]);
            String voteName = argsParts[1];
            topic.deleteVote(voteName, currentUser);
            System.out.println("Голосование  " + voteName + " удалено");
        } else {
            throw new IllegalArgumentException("Используйте: delete -t=<topic> -v=<vote>");
        }
    }

    private void vote(String args) {
        checkIsLogin();

        if (args.startsWith("-t=")
                && args.contains("-v=")) {
            String argsStr = args.substring(3);
            String[] argsParts = argsStr.split(" -v=");
            Topic topic = topics.get(argsParts[0]);
            String voteName = argsParts[1];
            Vote vote = topic.getVote(voteName);
            System.out.println("Запущено голосование  " + voteName);
            vote.getOptionsSet().forEach(System.out::println);
            System.out.print("Введите выбранный вариант ответа:");
            String option = scanner.nextLine();
            vote.addVote(option, currentUser);
            System.out.println("Голос за вариант " + option + " учтен");
            saveData();
        } else {
            throw new IllegalArgumentException("Используйте: vote -t=<topic> -v=<vote>");
        }
    }


    private boolean process(String input) throws IOException {
        String[] parts = input.split(" ", 2);
        String command = parts[0];
        String args = parts.length > 1 ? parts[1] : "";

        switch (command) {
            case "login":
                login(args);
                break;
            case "create":
                create(args);
                break;
            case "view":
                view(args);
                break;
            case "vote":
                vote(args);
                break;
            case "delete":
                delete(args);
                break;
            case "save":
                saveData();
                break;
            case "help":
                help();
                break;
            case "exit":
                saveData();
                System.out.println("Работа завершена");
                return true;
            default:
                System.out.println("Неизвестная команда. Введите 'help' для справки.");
        }
        return false;
    }

    public static void main(String[] args) {
        new VotingService().start();
    }
}
