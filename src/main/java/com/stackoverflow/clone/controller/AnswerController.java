package com.stackoverflow.clone.controller;

import com.stackoverflow.clone.entity.*;
import com.stackoverflow.clone.service.AnswerService;
import com.stackoverflow.clone.service.QuestionService;
import com.stackoverflow.clone.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/answer")
public class AnswerController {

    private final QuestionService questionService;
    private final AnswerService answerService;
    private final UserService userService;

    public AnswerController(QuestionService questionService, AnswerService answerService, UserService userService) {
        this.questionService = questionService;
        this.answerService = answerService;
        this.userService = userService;
    }

    @PostMapping("/save")
    public String saveAnswer(@RequestParam("questionId") Long questionId,
                             @RequestParam(value = "id", required = false) Long id,
                             @RequestParam("theAnswer") String theAnswer,
                             @ModelAttribute("answer") Answer answer) {

        Question question = questionService.findById(questionId);
        if (id != null) {
            answer = answerService.findById(id);
            answer.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
            answer.setTheAnswer(theAnswer);
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(authentication.getName());

        answer.setUser(user);
        answer.setQuestion(question);
        answerService.save(answer);
        return "redirect:/question/" + questionId;
    }

    @PostMapping("/updateAnswer")
    public String updateAnswer(@RequestParam("answerId") Long answerId,
                               Model model) {
        Answer tempAnswer = answerService.findById(answerId);
        model.addAttribute("question", tempAnswer.getQuestion());
        model.addAttribute("tempAnswer", tempAnswer);
        return "answer/edit-answer";
    }

    @PostMapping("/delete")
    public String deleteAnswer(@RequestParam("questionId") Long questionId,
                               @RequestParam("answerId") Long answerId) {
        answerService.deleteById(answerId);
        return "redirect:/question/" + questionId;
    }

    @PostMapping("/question/upvote/{questionId}/{answerId}/{voteType}")
    public String upvote(@PathVariable Long questionId, @PathVariable VoteType voteType,
                         @PathVariable Long answerId, Model model) {
        Answer answer = answerService.findById(answerId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(authentication.getName());
        Vote existingVote = answerService.findVoteByUserAndAnswer(user, answer);

        if (existingVote != null) {

            if (existingVote.getVoteType() == voteType.NOVOTE) {
                existingVote.setVoteType(voteType);


                Integer voteCount = answer.getVoteCount();

                answer.setVoteCount(voteCount + 1);

            } else if (existingVote.getVoteType() == voteType.DOWNVOTE){


                existingVote.setVoteType(voteType.UPVOTE);
                Integer voteCount = answer.getVoteCount();
                answer.setVoteCount(voteCount + 2);

            }
            else {
                existingVote.setVoteType(voteType.NOVOTE);
                Integer voteCount = answer.getVoteCount();
                answer.setVoteCount(voteCount - 1);
            }
        }
        else {
            Vote vote = new Vote();
            vote.setAnswer(answer);
            vote.setUser(user);
            vote.setVoteType(voteType);
            questionService.createVote(vote);
            Integer voteCount = answer.getVoteCount();
            answer.setVoteCount(voteCount + 1);

        }
        model.addAttribute("voteType", voteType);

        answerService.save(answer);

        return "redirect:/question/" + questionId;
    }

    @PostMapping("/question/downvote/{questionId}/{answerId}/{voteType}")
    public String downVote(@PathVariable Long questionId, @PathVariable VoteType voteType,
                           @PathVariable Long answerId, Model model) {
        Answer answer = answerService.findById(answerId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(authentication.getName());
        Vote existingVote = answerService.findVoteByUserAndAnswer(user, answer);
        if (existingVote != null) {
            if (existingVote.getVoteType() == voteType.NOVOTE) {

                existingVote.setVoteType(voteType);

                int voteCount = answer.getVoteCount();
                answer.setVoteCount(voteCount - 1);

            }
            else if (existingVote.getVoteType() == voteType.UPVOTE){
                existingVote.setVoteType(voteType.DOWNVOTE);
                Integer voteCount = answer.getVoteCount();
                answer.setVoteCount(voteCount - 2);

            }
            else{
                existingVote.setVoteType(voteType.NOVOTE);
                Integer voteCount = answer.getVoteCount();
                answer.setVoteCount(voteCount + 1);
            }

        } else {
            Vote vote = new Vote();
            vote.setAnswer(answer);
            vote.setUser(user);
            vote.setVoteType(voteType);
            questionService.createVote(vote);

            int voteCount = answer.getVoteCount();
            answer.setVoteCount(voteCount -1);

        }
        model.addAttribute("voteType", voteType);
        answerService.save(answer);

        return "redirect:/question/" + questionId;
    }

    @PostMapping("/verify/{answerId}")
    public String verifyAnswer(@RequestParam("verify") String verify,
                               @PathVariable("answerId") Long answerId,
                               @RequestParam(value = "questionId", required = false) Long questionId,
                               @RequestParam("userId") Long userId){
        Answer answer = answerService.findById(answerId);
        answer.setVerified(verify);
        answerService.save(answer);
        if (questionId != null){
            return "redirect:/question/" + questionId;
        }
        return "redirect:/answers/" + userId;
    }

    @PostMapping("/viewAnswer/{answerId}")
    public String veiwAnswer(@PathVariable("answerId") Long answerId,
                             Model model){
        Answer answer = answerService.findById(answerId);
        model.addAttribute("answer", answer);
        return"answer/view-answer";
    }
}
