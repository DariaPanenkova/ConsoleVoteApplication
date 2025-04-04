package Server;

import App.Topic;
import App.Vote;
import com.google.gson.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.Type;

import com.google.gson.reflect.TypeToken;

public class VotingStorage {
    private final Map<String, Topic> topics = new HashMap<>();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private String currentUser;

    private String loadData(String filename) {
        try (Reader reader = new FileReader(filename)) {
            Type type = new TypeToken<HashMap<String, Topic>>(){}.getType();
            Map<String, Topic> loaded = gson.fromJson(reader, type);
            topics.clear();
            topics.putAll(loaded);
            return "Загружены данные из файла: " + filename;
        } catch (IOException e) {
            return "Ошибка загрузки данных из файла: " + e.getMessage();
        }
    }

    private String saveData(String filename) {
        try (Writer writer = new FileWriter(filename)) {
            gson.toJson(topics, writer);
            return "Данные сохранены в файл: " + filename;
        } catch (IOException e) {
            return "Ошибка сохранения данных в файл: " + e.getMessage();
        }
    }

    private String login(String args) {
        currentUser = args;
        return "OK|Вы вошли как: " + currentUser;
    }

    private void checkIsLogin() {
        if (currentUser == null) {
            throw new IllegalStateException("Сначала выполните вход (login)");
        }
    }

    private String view(String[] args) {
        checkIsLogin();

        if (args.length == 0) {
            return listTopics();
        } else if (args.length == 1) {
            return listVotesInTopic(args[0]);
        } else if (args.length == 2){
            return viewVoteResult(args[0], args[1]);
        } else {
            throw new IllegalArgumentException("Ошибочное количество аргументов");
        }
    }

    private String listTopics() {
        if (topics.isEmpty()) {
            return "OK|Разделы не созданы";
        }

        StringBuilder result = new StringBuilder();

        topics.forEach((topicName, topic) -> {
            result.append(String.format("<%s(votes in topic=%d)>%n", topicName, topic.getVotesCount()));
        });
        return "OK|" + result.toString();
    }

    private String listVotesInTopic(String topicName) {
        Topic topic = topics.get(topicName);
        if (topic == null) {
            throw new IllegalArgumentException("Раздел не найден");
        }
        if (topic.getVotesCount() == 0){
            return "OK|В разделе не создано голосований";
        }
        return "OK|" + String.join("\n", topic.getSetVotesName());
    }

    private String viewVoteResult(String topicName, String voteName) {
        Topic topic = topics.get(topicName);
        if (topic == null) {
            throw new IllegalArgumentException("Раздел не найден");
        }
        if (topic.getVotesCount() == 0){
            return "OK|В разделе не создано голосований";
        }
        Vote vote = topic.getVote(voteName);

        StringBuilder result = new StringBuilder("OK|" + vote.getVoteName() + "\n");
        vote.getOptionsMap().forEach((option, voteCount) -> {
            result.append(String.format("%s:%d%n", option, voteCount));
        });

        return result.toString();
    }

    private String createTopic(String topicName) {
        checkIsLogin();

        if (topics.containsKey(topicName)) {
            throw new IllegalArgumentException("Раздел уже существует");
        }

        Topic topic = new Topic.TopicBuilder().topicName(topicName).build();
        topics.put(topicName, topic);
        return "OK|Раздел создан: " + topicName;
    }

    private String createVote(String[] args) {
        // Формат: create_vote|topic|user|voteName|description|option1|option2|...
        Topic topic = topics.get(args[0]);
        if (topic == null) return "ERROR|Topic not found";

        Vote.VoteBuilder builder = new Vote.VoteBuilder()
                .voteName(args[2])
                .description(args[3])
                .loginCreator(args[1])
                .maxOptions(args.length - 4);

        for (int i = 4; i < args.length; i++) {
            builder.addOption(args[i]);
        }

        topic.addVote(builder.build());
        return "OK|Vote created: " + args[2];
    }
    public String handleCommand(String commandStr) {
        try {
            String[] parts = commandStr.split("\\|");
            String cmd = parts[0];
            String[] args = Arrays.copyOfRange(parts, 1, parts.length);

            switch (cmd) {
                case "load":
                    return loadData(args[0]);
                case "save":
                    return saveData(args[0]);
                case "login":
                    return login(args[0]);
                case "view":
                    return view(args);
                case "createTopic":
                    return createTopic(args[0]);
                case "createVote":
                    return createVote(args);
                case "vote":
                    return vote(args);
                case "delete":
                    return deleteVote(args[0], args[1], args[2]);
                case "exit":
                    return "OK|Server shutting down";
                default:
                    return "ERROR|Unknown command";
            }
        } catch (Exception e) {
            return "ERROR|" + e.getMessage();
        }
    }

    private String vote(String[] args) {
        // Формат: vote|topic|voteName|option|user
        Topic topic = topics.get(args[0]);
        if (topic == null) return "ERROR|Topic not found";

        Vote vote = topic.getVote(args[1]);
        vote.addVote(args[2], args[3]);
        return "OK|Vote accepted";
    }


    private String deleteVote(String topic, String vote, String user) {
        topics.get(topic).deleteVote(vote, user);
        return "OK|Vote deleted";
    }
}