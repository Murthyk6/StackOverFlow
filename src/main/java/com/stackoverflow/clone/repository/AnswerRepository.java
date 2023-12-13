package com.stackoverflow.clone.repository;

import com.stackoverflow.clone.entity.Answer;
import com.stackoverflow.clone.entity.Question;
import com.stackoverflow.clone.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    List<Answer> findByQuestionId(Long id);
    List<Answer> findFirst5ByUserOrderByCreatedAtDesc(User user);
    Page<Answer> findAllByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    @Query("SELECT a FROM Answer a " +
            "INNER JOIN FETCH a.question q " +
            "WHERE q.user = :user AND a.verified = 'NA' " +
            "ORDER BY a.createdAt DESC")
    List<Answer> findUnverifiedAnswersByUser(@Param("user") User user);
}
