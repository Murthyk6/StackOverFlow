package com.stackoverflow.clone.controller;

import ch.qos.logback.classic.Logger;
import com.stackoverflow.clone.entity.Answer;
import com.stackoverflow.clone.entity.Question;
import com.stackoverflow.clone.entity.Tag;
import com.stackoverflow.clone.entity.User;
import com.stackoverflow.clone.service.AnswerService;
import com.stackoverflow.clone.service.QuestionService;
import com.stackoverflow.clone.service.UserService;
import com.stackoverflow.clone.util.TimeElapsedFormatter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.*;

@Controller
public class UserController {
    private final QuestionService questionService;
    private final UserService userService;
    private final AnswerService answerService;

    public UserController(QuestionService questionService, UserService userService, AnswerService answerService){
        this.questionService = questionService;
        this.userService = userService;
        this.answerService = answerService;
    }

    @GetMapping("/profile")
    public String profile(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(authentication.getName());
        List<Question> userQuestions = questionService.findByUser(user);

        model.addAttribute("userQuestions", userQuestions);
        return "user/profile";
    }

    @GetMapping("/users")
    public String users(@RequestParam(name = "userSearch",defaultValue = "") String userSearch,
                        @RequestParam(name ="tab", defaultValue = "popular") String tab,
                        @RequestParam(defaultValue = "1") int page,
                        Model model){

        Pageable pageable = PageRequest.of(page - 1, 8);
        Page<User> users = userService.searchAndSortByUsernameOrName(userSearch,tab,pageable);

        for (User user : users) {
            user.setTopTags(userService.findTop3TagsByUserId(user.getId()));
        }

        if(tab != null && !tab.equals("")) {
            model.addAttribute("tab",tab);
        }
        if(userSearch != null && !userSearch.equals("")){
            model.addAttribute("userSearch", userSearch);
        }

        model.addAttribute("totalPages", users.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("users", users);
        return "user/user-list";
    }

    @GetMapping("/users/{userId}")
    public String profile(@PathVariable("userId") Long userId,
                          @RequestParam(value = "allAnswers", required = false) String allAnswers,
                          Model model){
        Optional<User> user = userService.findById(userId);
        if (user.isPresent()){
            String memberSince = TimeElapsedFormatter.formatTimeMember(user.get().getCreatedAt());
            if (allAnswers != null){

                Pageable pageable = PageRequest.of(0, 5);

                Page<Answer> answers = answerService.findAllByUserOrderByCreatedAtDesc(user.get(), pageable);
                model.addAttribute("user", user.get());
                model.addAttribute("topAnswers", answers);
                model.addAttribute("allAnswers", allAnswers);
                return "user/profile";
            }

            List<Tag> topTags = userService.findTopTags(userId);
            List<Question> qusetion = questionService.findFirst5ByUserOrderByCreatedAtDesc(user.get());
            List<Answer> answers = answerService.findFirst5ByUserOrderByCreatedAtDesc(user.get());
            model.addAttribute("topAnswers", answers);
            model.addAttribute("topQuestion", qusetion);
            model.addAttribute("topTags", topTags);
            model.addAttribute("user", user.get());
            model.addAttribute("memberSince", memberSince);
            return "user/profile";
        }
        model.addAttribute("errorUser","User Not Found");
        return "user/profile";
    }


    @PostMapping("/users/edit/{userId}")
    public String editProfile(@PathVariable("userId") Long userId, Model model){


        Optional<User> user = userService.findById(userId);

        if (user.isPresent()) {
            String name = user.get().getName();
                String avatarUrl = "https://via.placeholder.com/100/007bff/ffffff?text=";

            if (name != null && !name.isEmpty()) {
                avatarUrl += name.charAt(0);
            } else {
                avatarUrl += "Unknown";
            }

            model.addAttribute("user", user.get());
            model.addAttribute("avatarUrl", avatarUrl);
            return "user/edit-profile";
        } else {
            // Handle the case where user is not found
            model.addAttribute("errorUser", "User Not Found");
            return "user/profile";
        }
    }




    @PostMapping("/users/save/{userId}")
    public String saveProfile(@PathVariable Long userId, @ModelAttribute User updatedUser) {

        User user = userService.findById(userId).orElse(null);

        if (user != null) {

            user.setName(updatedUser.getName());
            user.setEmail(updatedUser.getEmail());
            user.setInfo(updatedUser.getInfo());

            userService.save(user);

            return "redirect:/users/" + userId;
        } else {

            return "redirect:/error-page";
        }
    }

    @GetMapping("/answers/{userId}")
    public String answers(@PathVariable(name = "userId") Long userId,
                          Model model){
        Optional<User> user = userService.findById(userId);
        List<Answer> answers = answerService.findUnverifiedAnswersByUser(user.get());

        model.addAttribute("answers", answers);
        return "user/answers";
    }
}
