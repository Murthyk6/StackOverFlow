package com.stackoverflow.clone.repository;

import com.stackoverflow.clone.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByAnswerId(Long id);
}
