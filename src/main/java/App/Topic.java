package App;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Topic {
    private final String topicName;
    private final Map<String, Vote> voteMap;

    private Topic(TopicBuilder builder) {
        this.topicName = builder.topicName;
        this.voteMap = new HashMap<>();
    }

    public static class TopicBuilder {
        private String topicName;

        public TopicBuilder topicName(String topicName) {
            this.topicName = topicName;
            return this;
        }

        public Topic build() {
            if (topicName == null || topicName.isBlank()) {
                throw new IllegalStateException("Название раздела обязательно");
            }
            return new Topic(this);
        }
    }

    public void addVote(Vote vote) {
        Objects.requireNonNull(vote, "Голосования не существует");
        if (voteMap.containsKey(vote.getVoteName())) {
            throw new IllegalArgumentException("Голосование с таким именем уже существует");
        }
        voteMap.put(vote.getVoteName(), vote);
    }

    public void deleteVote(String voteName, String userLogin) {
        Vote vote = getVote(voteName);
        if (!vote.isCreatedBy(userLogin)) {
            throw new SecurityException("Только создатель может удалить голосование");
        }
        voteMap.remove(voteName);
    }

    @Override
    public String toString() {
        StringBuilder votesStr = new StringBuilder("{");
        boolean first = true;

        for (Map.Entry<String, Vote> entry : voteMap.entrySet()) {
            if (!first) {
                votesStr.append(", ");
            }
            votesStr.append(entry.getKey());
            first = false;
        }
        votesStr.append("}");

        return "Topic{" +
                "topicName= '" + topicName + '\'' +
                ", votesCount= " + voteMap.size() +
                ", votes= " + votesStr +
                '}';
    }

    public String getTopicName() {
        return this.topicName;
    }

    public int getVotesCount() {
        return this.voteMap.size();
    }

    public Set<String> getSetVotesName() {
        return this.voteMap.keySet();
    }

    public Vote getVote(String voteName) {
        Vote vote = voteMap.get(voteName);
        if (vote == null) {
            throw new IllegalArgumentException("Голосование " + voteName + " не найдено");
        }
        return vote;
    }

    public boolean hasVote(String voteName) {
        Vote vote = voteMap.get(voteName);
        if (vote == null) {
            return false;
        } else {
            return true;
        }
    }
}
