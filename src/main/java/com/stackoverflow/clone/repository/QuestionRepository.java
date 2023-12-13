package com.stackoverflow.clone.repository;

import com.stackoverflow.clone.entity.Question;
import com.stackoverflow.clone.entity.Tag;
import com.stackoverflow.clone.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question,Long> {
    int countQuestionsByTags(Tag tag);
    List<Question> findByUser(User user);
    List<Question> findTop10ByOrderByCreatedAtDesc();
    @Query("SELECT q FROM Question q WHERE q.title LIKE %:search% OR q.problem LIKE %:search%")
    List<Question> findQuestionsBySearch(@Param("search") String search);

    List<Question> findAllByUserName(String username);

    @Query("SELECT q FROM Question q JOIN q.tags t WHERE q.user.id = :userId AND t.name = :tagName")
    List<Question> findQuestionsByUserAndTag(@Param("userId") Long userId, @Param("tagName") String tagName);

    List<Question> findFirst5ByUserOrderByCreatedAtDesc(User user);


    @Query("SELECT COUNT(a.id) " +
            "FROM Question q " +
            "LEFT JOIN q.answers a " +
            "WHERE q.id = :questionId " +
            "AND a.verified = 'ok'")
    Long findQuestionWithVerifiedAnswerCount(@Param("questionId") Long questionId);
//    List<Question> findQuestionWithVerifiedAnswerCount();
}
