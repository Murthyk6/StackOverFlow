package com.stackoverflow.clone.entity;

import jakarta.persistence.*;

@Entity
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long voteId;

    @Enumerated(EnumType.STRING)
    private VoteType voteType; // Enum for UPVOTE or DOWNVOTE

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // Assuming a User entity for the voter

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne
    @JoinColumn(name = "answer_id")
    private Answer answer;

    public Vote() {
    }

    public Vote(Long voteId, VoteType voteType, User user, Question question) {
        this.voteId = voteId;
        this.voteType = voteType;
        this.user = user;
        this.question = question;
    }

    public Long getVoteId() {
        return voteId;
    }

    public void setVoteId(Long voteId) {
        this.voteId = voteId;
    }

    public VoteType getVoteType() {
        return voteType;
    }

    public void setVoteType(VoteType voteType) {
        this.voteType = voteType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "Vote{" +
                "voteId=" + voteId +
                ", voteType=" + voteType +
                ", user=" + user +
                ", question=" + question +
                ", answer=" + answer +
                '}';
    }
}
