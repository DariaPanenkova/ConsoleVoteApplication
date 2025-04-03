import App.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class TopicTest {
    private Topic topic;
    private Vote vote1;
    private Vote vote2;

    @BeforeEach
    void setUp() {
        topic = new Topic.TopicBuilder().topicName("Раздел 1").build();
        vote1 = new Vote.VoteBuilder()
                .voteName("Опрос 1")
                .loginCreator("admin")
                .maxOptions(2)
                .addOption("Вариант 1")
                .addOption("Вариант 2")
                .build();
        vote2 = new Vote.VoteBuilder()
                .voteName("Опрос 2")
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
        assertEquals(0, topic.getVotesCount());
    }

    @Test
    @DisplayName("Test adding votes")
    void testAddVote() {
        topic.addVote(vote1);
        assertEquals(1, topic.getVotesCount());

        topic.addVote(vote2);
        assertEquals(2, topic.getVotesCount());
    }

    @Test
    @DisplayName("Test removing vote by creator")
    void testRemoveVoteByCreator() {
        topic.addVote(vote1);
        topic.addVote(vote2);
        topic.deleteVote("Опрос 2", "admin");
        assertEquals(1, topic.getVotesCount());
    }

    @Test
    @DisplayName("Test removing vote by non-creator")
    void testRemoveVoteByNonCreator() {
        topic.addVote(vote1);
        topic.addVote(vote2);
        assertThrows(SecurityException.class, () -> topic.deleteVote("Опрос 2", "user"));
        assertEquals(2, topic.getVotesCount());
    }
}
