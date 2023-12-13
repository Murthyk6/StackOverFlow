package com.stackoverflow.clone.service.implementation;

import com.stackoverflow.clone.entity.Comment;
import com.stackoverflow.clone.repository.CommentRepository;
import com.stackoverflow.clone.service.CommentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    public CommentServiceImpl(CommentRepository commentRepository){
        this.commentRepository = commentRepository;
    }

    @Override
    public void save(Comment comment) {
        commentRepository.save(comment);
    }

    @Override
    public List<Comment> findByAnswerId(Long id) {
        return commentRepository.findByAnswerId(id);
    }

    @Override
    public void deleteById(Long id) {
        commentRepository.deleteById(id);
    }

    @Override
    public Comment findById(Long commentId) {
        return commentRepository.findById(commentId).get();
    }
}
