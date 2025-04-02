package App;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Topic {
    private final String topicName;
    private final Map<String, Survey> surveyMap;

    private Topic(TopicBuilder builder) {
        this.topicName = builder.topicName;
        this.surveyMap = new HashMap<>();
    }

    public static class TopicBuilder {
        private String topicName;

        public TopicBuilder topicName(String topicName) {
            this.topicName = topicName;
            return this;
        }

        public Topic build() {
            if (topicName == null || topicName.isBlank()) {
                throw new IllegalStateException("Название темы обязательно");
            }
            return new Topic(this);
        }
    }

    public void addSurvey(Survey survey) {
        Objects.requireNonNull(survey, "Опроса не существует");
        if (surveyMap.containsKey(survey.getSurveyName())) {
            throw new IllegalArgumentException("Опрос с таким именем уже существует");
        }
        surveyMap.put(survey.getSurveyName(), survey);
    }

    @Override
    public String toString() {
        StringBuilder surveysStr = new StringBuilder("{");
        boolean first = true;

        for (Map.Entry<String, Survey> entry : surveyMap.entrySet()) {
            if (!first) {
                surveysStr.append(", ");
            }
            surveysStr.append(entry.getKey());
            first = false;
        }
        surveysStr.append("}");

        return "Topic{" +
                "topicName= '" + topicName + '\'' +
                ", surveysCount= " + surveyMap.size() +
                ", surveys= " + surveysStr +
                '}';
    }

    public String getTopicName(){
        return this.topicName;
    }

    public int getSurveysCount(){
        return this.surveyMap.size();
    }
}
