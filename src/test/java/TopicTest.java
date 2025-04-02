import App.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class TopicTest {
    private Topic topic;
    private Survey survey1;
    private Survey survey2;

    @BeforeEach
    void setUp() {
        topic = new Topic.TopicBuilder().topicName("Раздел 1").build();
        survey1 = new Survey.SurveyBuilder()
                .surveyName("Опрос 1")
                .loginCreator("admin")
                .maxOptions(2)
                .addOption("Вариант 1")
                .addOption("Вариант 2")
                .build();
        survey2 = new Survey.SurveyBuilder()
                .surveyName("Опрос 2")
                .loginCreator("admin")
                .maxOptions(2)
                .addOption("Вариант 1")
                .addOption("Вариант 2")
                .build();
    }

    @Test
    @DisplayName("Test topic creation")
    void testTopicCreation() {
        assertNotNull(topic);
        assertEquals("Раздел 1", topic.getTopicName());
        assertEquals(0, topic.getSurveysCount());
    }

    @Test
    @DisplayName("Test adding surveys")
    void testAddVote() {
        topic.addSurvey(survey1);
        assertEquals(1, topic.getSurveysCount());

        topic.addSurvey(survey2);
        assertEquals(2, topic.getSurveysCount());
    }
}
