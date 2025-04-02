package App;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Survey {
    private final String surveyName;
    private final String description;
    private final String loginCreator;
    private final int maxOptions;
    private final Map<String, Integer> optionsMap;

    public Survey(String surveyName, String description, String loginCreator, int maxOptions, List<String> options){
        this.surveyName = surveyName;
        this.description = description;
        this.loginCreator = loginCreator;
        this.maxOptions = maxOptions;
        this.optionsMap = new HashMap<>();

        for (String option : options) {
            this.optionsMap.put(option, 0);
        }
    }

    public Survey(SurveyBuilder surveyBuilder){
        this.surveyName = surveyBuilder.surveyName;
        this.description = surveyBuilder.description;
        this.loginCreator = surveyBuilder.loginCreator;
        this.maxOptions = surveyBuilder.maxOptions;
        this.optionsMap = new HashMap<>();

        for (String option : surveyBuilder.options) {
            this.optionsMap.put(option, 0);
        }
    }

    public static class SurveyBuilder {
        private String surveyName;
        private String description;
        private String loginCreator;
        private int    maxOptions;
        private List<String> options = new ArrayList<>();

        public SurveyBuilder() {}

        public SurveyBuilder surveyName(String surveyName) {
            this.surveyName = surveyName;
            return this;
        }

        public SurveyBuilder description(String description) {
            this.description = description;
            return this;
        }

        public SurveyBuilder loginCreator(String loginCreator) {
            this.loginCreator = loginCreator;
            return this;
        }

        public SurveyBuilder maxOptions(int maxOptions) {
            if (maxOptions <= 0) {
                throw new IllegalArgumentException("Максимальное количество вариантов должно быть положительным");
            }
            this.maxOptions = maxOptions;
            return this;
        }

        public SurveyBuilder addOption(String option) {
            if (options.size() > maxOptions) {
                throw new IllegalArgumentException(
                        String.format("Превышено максимальное количество вариантов (%d)", maxOptions)
                );
            }
            this.options.add(option);
            return this;
        }

        public SurveyBuilder options(List<String> options) {
            if (options.size() > maxOptions) {
                throw new IllegalArgumentException(
                        String.format("Превышено максимальное количество вариантов (%d)", maxOptions)
                );
            }
            this.options = new ArrayList<>(options);
            return this;
        }

        public Survey build() {
            if (surveyName == null || surveyName.isBlank()) {
                throw new IllegalStateException("Название голосования не может быть пустым");
            }
            if (loginCreator == null || loginCreator.isBlank()) {
                throw new IllegalStateException("Создатель голосования не может быть пустым");
            }
            if (options == null || options.isEmpty()) {
                throw new IllegalStateException("Должен быть хотя бы один вариант ответа");
            }
            return new Survey(this);
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

        return "Survey{" +
                "surveyName='" + surveyName + "\',\n" +
                "description='" + description + "\',\n" +
                "loginCreator='" + loginCreator + "\',\n" +
                "options=" + optionsStr.toString() +
                '}';
    }

    public void addVote(String option) {
        if (!optionsMap.containsKey(option)) {
            throw new IllegalArgumentException("Варианта ответа " + option + "не существует");
        }
        optionsMap.put(option, optionsMap.get(option) + 1);
    }

    public String getSurveyName(){
        return this.surveyName;
    }

    public boolean isCreatedBy(String userLogin) {
        return loginCreator.equals(userLogin);
    }


}
