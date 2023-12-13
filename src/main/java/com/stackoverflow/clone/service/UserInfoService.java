package com.stackoverflow.clone.service;

import com.stackoverflow.clone.entity.Answer;
import com.stackoverflow.clone.entity.User;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Component
public class UserInfoService {

    private final UserService userService;
    private final AnswerService answerService;

    public UserInfoService(UserService userService, AnswerService answerService) {
        this.userService = userService;
        this.answerService = answerService;
    }

    public User currentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            User user = userService.findByUsername(authentication.getName());
            return user;
        }
        return new User();
    }

    public boolean answers(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            User user = userService.findByUsername(authentication.getName());
            List<Answer> answers = answerService.findUnverifiedAnswersByUser(user);
            if (answers != null && !answers.isEmpty()){
                return true;
            }
            else {
                return false;
            }
        }
        return false;
    }
    public int answerCount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            User user = userService.findByUsername(authentication.getName());
            List<Answer> answers = answerService.findUnverifiedAnswersByUser(user);
            if (answers != null) {
                return answers.size();
            }
        }
        return 0; // Return 0 if there are no unverified answers or if no user is authenticated.
    }
}
