package com.stackoverflow.clone.repository;

import com.stackoverflow.clone.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VoteRepository extends JpaRepository<Vote,Long> {

    Vote findVoteByUserAndQuestion(User user, Question question);

    Vote findVoteByUserAndAnswer(User user, Answer answer);
}
