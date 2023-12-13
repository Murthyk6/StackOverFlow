package com.stackoverflow.clone.service;

import com.stackoverflow.clone.entity.Question;
import com.stackoverflow.clone.entity.Tag;
import com.stackoverflow.clone.entity.User;
import com.stackoverflow.clone.entity.Vote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QuestionService {
    void save(Question question);

    Question findById(Long questionId);

    List<Question> findAll();

    Question deleteById(Long deleteId);

    int countQuestionsByTag(Tag tag);
    List<Question> findByUser(User user);
    List<Question> findTop10ByOrderByCreatedAtDesc();

    List<Question> findQuestionsBySearch(String search);

    List<Question> findAllByUserName(String username);

    List<Question> findQuestionsByUserAndTag(Long userId, String tagName);
    List<Question> findFirst5ByUserOrderByCreatedAtDesc(User user);

    void updateVote(Vote existingVote);

    Vote findVoteByUserAndQuestion(User user, Question question);

    void createVote(Vote vote);

    Page<Question> searchAndSortByNewOrUnansweredOrScore(List<Question> questions, String tab, Pageable pageable);

    Long findQuestionWithVerifiedAnswerCount(Question question);
}
