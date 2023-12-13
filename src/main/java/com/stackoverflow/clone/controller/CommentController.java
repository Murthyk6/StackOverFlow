package com.stackoverflow.clone.controller;

import com.stackoverflow.clone.entity.Answer;
import com.stackoverflow.clone.entity.Comment;
import com.stackoverflow.clone.entity.User;
import com.stackoverflow.clone.service.AnswerService;
import com.stackoverflow.clone.service.CommentService;
import com.stackoverflow.clone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;
    private final AnswerService answerService;
    private final UserService userService;

    @Autowired
    public CommentController(CommentService commentService, AnswerService answerService, UserService userService) {
        this.commentService = commentService;
        this.answerService = answerService;
        this.userService = userService;
    }

    @PostMapping("/add")
    public String addComment(@RequestParam("answerId") Long answerId,
                             @RequestParam("theComment") String theComment,
                             @RequestParam(value = "commentId", required = false) Long commentId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user= userService.findByUsername(authentication.getName());
        Answer answer = answerService.findById(answerId);
        Comment comment;
        if(commentId!=null) {
            comment = commentService.findById(commentId);
        } else {
            comment = new Comment();
        }
        comment.setTheComment(theComment);
        comment.setUser(user);
        comment.setAnswer(answer);
        commentService.save(comment);

        return "redirect:/question/" + answer.getQuestion().getId();
    }

    @PostMapping("/delete")
    public String addComment(@RequestParam("answerId") Long answerId,
                             @RequestParam("commentId") Long commentId) {
        commentService.deleteById(commentId);
        Answer answer = answerService.findById(answerId);
        return "redirect:/question/" + answer.getQuestion().getId();
    }
}
