package App;

import java.util.*;

public class Vote {
    private final String voteName;
    private final String description;
    private final String loginCreator;
    private final int maxOptions;
    private Map<String, Integer> optionsMap;
    private Set<String> votedUsersSet;

    public Vote(String voteName, String description, String loginCreator, int maxOptions, List<String> options){
        this.voteName = voteName;
        this.description = description;
        this.loginCreator = loginCreator;
        this.maxOptions = maxOptions;
        this.optionsMap = new HashMap<>();
        for (String option : options) {
            this.optionsMap.put(option, 0);
        }
        this.votedUsersSet = new HashSet<>();
    }

    public Vote(VoteBuilder voteBuilder){
        this.voteName = voteBuilder.voteName;
        this.description = voteBuilder.description;
        this.loginCreator = voteBuilder.loginCreator;
        this.maxOptions = voteBuilder.maxOptions;
        this.optionsMap = new HashMap<>();

        for (String option : voteBuilder.options) {
            this.optionsMap.put(option, 0);
        }

        this.votedUsersSet = new HashSet<>();
    }

    public static class VoteBuilder {
        private String voteName;
        private String description;
        private String loginCreator;
        private int    maxOptions;
        private List<String> options = new ArrayList<>();

        public VoteBuilder() {}

        public VoteBuilder voteName(String voteName) {
            this.voteName = voteName;
            return this;
        }

        public VoteBuilder description(String description) {
            this.description = description;
            return this;
        }

        public VoteBuilder loginCreator(String loginCreator) {
            this.loginCreator = loginCreator;
            return this;
        }

        public VoteBuilder maxOptions(int maxOptions) {
            if (maxOptions <= 0) {
                throw new IllegalArgumentException("Максимальное количество вариантов должно быть положительным");
            }
            this.maxOptions = maxOptions;
            return this;
        }

        public VoteBuilder addOption(String option) {
            if (options.size() > maxOptions) {
                throw new IllegalArgumentException(
                        String.format("Превышено максимальное количество вариантов (%d)", maxOptions)
                );
            }
            this.options.add(option);
            return this;
        }

        public VoteBuilder options(List<String> options) {
            if (options.size() > maxOptions) {
                throw new IllegalArgumentException(
                        String.format("Превышено максимальное количество вариантов (%d)", maxOptions)
                );
            }
            this.options = new ArrayList<>(options);
            return this;
        }

        public Vote build() {
            if (voteName == null || voteName.isBlank()) {
                throw new IllegalStateException("Название голосования не может быть пустым");
            }
            if (loginCreator == null || loginCreator.isBlank()) {
                throw new IllegalStateException("Создатель голосования не может быть пустым");
            }
            if (options == null || options.isEmpty()) {
                throw new IllegalStateException("Должен быть хотя бы один вариант ответа");
            }
            return new Vote(this);
        }
    }

    @Override
    public String toString() {
        StringBuilder optionsStr = new StringBuilder("{");
        boolean first = true;

        for (Map.Entry<String, Integer> entry : optionsMap.entrySet()) {
            if (!first) {
                optionsStr.append(", ");
            }
            optionsStr.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue());
            first = false;
        }
        optionsStr.append("}");

        return "Vote{" +
                "voteName='" + voteName + "\',\n" +
                "description='" + description + "\',\n" +
                "loginCreator='" + loginCreator + "\',\n" +
                "options=" + optionsStr.toString() +
                '}';
    }

    public void addVote(String option, String userLogin) {
        if (votedUsersSet.contains(userLogin)){
            throw new IllegalArgumentException("Пользователь  " + userLogin + " уже отдал свой голос");
        }
        if (!optionsMap.containsKey(option)) {
            throw new IllegalArgumentException("Варианта ответа " + option + " не существует");
        }
        optionsMap.put(option, optionsMap.get(option) + 1);
        votedUsersSet.add(userLogin);
    }

    public String getVoteName(){
        return this.voteName;
    }

    public Map<String, Integer>  getOptionsMap(){
        return this.optionsMap;
    }

    public Set<String> getOptionsSet(){
        return this.optionsMap.keySet();
    }


    public boolean isCreatedBy(String userLogin) {
        return loginCreator.equals(userLogin);
    }

//    public void addOption(String option) {
//        if (optionsMap.size() > maxOptions) {
//            throw new IllegalArgumentException(
//                    String.format("Превышено максимальное количество вариантов (%d)", maxOptions)
//            );
//        }
//        this.optionsMap.put(option, 0);
//    }
}
