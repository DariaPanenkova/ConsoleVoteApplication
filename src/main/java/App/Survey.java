package App;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Survey {
    private String surveyName;
    private String description;
    private String loginCreator;
    private Map<String, Integer> optionsMap;

    public Survey(String surveyName, String description, String loginCreator, List<String> options){
        this.surveyName = surveyName;
        this.description = description;
        this.loginCreator = loginCreator;
        this.optionsMap = new HashMap<>();

        for (String option : options) {
            this.optionsMap.put(option, 0);
        }
    }

    public Survey(SurveyBuilder surveyBuilder){
        this.surveyName = surveyBuilder.surveyName;
        this.description = surveyBuilder.description;
        this.loginCreator = surveyBuilder.loginCreator;
        this.optionsMap = new HashMap<>();

        for (String option : surveyBuilder.options) {
            this.optionsMap.put(option, 0);
        }
    }

    public static class SurveyBuilder {
        private String surveyName;
        private String description;
        private String loginCreator;
        private List<String> options = new ArrayList<>();

        private SurveyBuilder() {}

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

        public SurveyBuilder addOption(String option) {
            this.options.add(option);
            return this;
        }

        public SurveyBuilder options(List<String> options) {
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
}
