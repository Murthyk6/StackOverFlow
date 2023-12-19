package com.stackoverflow.clone.controller;

import com.stackoverflow.clone.entity.*;
import com.stackoverflow.clone.service.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/")
public class QuestionController {

    private final QuestionService questionService;
    private final AnswerService answerService;
    private final TagService tagService;
    private final UserService userService;
    private final CommentService commentService;

    public QuestionController(QuestionService questionService, UserService userService,
                              TagService tagService, AnswerService answerService, CommentService commentService) {
        this.questionService = questionService;
        this.userService = userService;
        this.tagService = tagService;
        this.answerService = answerService;
        this.commentService = commentService;
    }

    @GetMapping("/")
    public String home(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(authentication.getName());
        List<Question> recent10Question = questionService.findTop10ByOrderByCreatedAtDesc();
        if (user != null) {
            model.addAttribute("user", user);
        }
        model.addAttribute("recent10Question", recent10Question);
        return "Home-Page";
    }

    @GetMapping("/questions")
    public String questions(Model model, @RequestParam(value = "tab", defaultValue = "new") String tab,
                            @RequestParam(value = "q", required = false) String tagSearch,
                            @RequestParam(defaultValue = "1") int page) {
        Page<Question> questionPage = null;
        Pageable pageable = PageRequest.of(page - 1, 6);
        List<Question> questions = new ArrayList<>();
        questions = questionService.findAll();
        questionPage = questionService.searchAndSortByNewOrUnansweredOrScore(questions, tab, pageable);

        for (Question question : questionPage) {
            question.setVerifiedCount(questionService.findQuestionWithVerifiedAnswerCount(question));
        }
        model.addAttribute("tab", tab);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", questionPage.getTotalPages());
        model.addAttribute("questions", questionPage);
        model.addAttribute("questionsSize", questions.size());
        model.addAttribute("q", tagSearch);
        return "all-question";
    }


    @GetMapping("/new-question")
    public String newQuestion(Model model) {
        Question question = new Question();
        model.addAttribute("question", question);
        return "question/new-question";
    }

    @PostMapping("/save")
    public String saveQuestion(@ModelAttribute("question") Question question,
                               @RequestParam(value = "voteCount", required = false) Integer voteCount,
                               @RequestParam(value = "tagNames", required = false) String tagNames) {
        if (question.getId() != null) {
            question.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(authentication.getName());
        Set<Tag> tags = tagService.findOrCreateTag(tagNames);

        if (voteCount != null) {
            question.setVoteCount(voteCount);
        }
        if (voteCount == null) {
            question.setVoteCount(0);
        }
        question.setUser(user);
        question.setTags(tags);
        questionService.save(question);
        return "redirect:/question/" + question.getId();
    }

    @GetMapping("/question/{questionId}")
    public String viewQuestion(@PathVariable("questionId") Long questionId,
                               Model model) {
        Question question = questionService.findById(questionId);
        List<Answer> answers = answerService.findByQuestionId(questionId);
        Answer answer = new Answer();

        for (Answer ans : answers) {
            List<Comment> comments = commentService.findByAnswerId(ans.getId());
            ans.setComments(comments);
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated()) {
            User user = userService.findByUsername(authentication.getName());
            Vote existingVote = questionService.findVoteByUserAndQuestion(user, question);
            if (existingVote != null) {
                model.addAttribute("voteType", existingVote.getVoteType());
            }
        }

        model.addAttribute("commentDeleted", true);
        model.addAttribute("user", question.getUser());
        model.addAttribute("answers", answers);
        model.addAttribute("answer", answer);
        model.addAttribute("question", question);
        return "question/view-question";
    }

    @PostMapping("/question/editQuestion/{questionId}")
    public String editPost(@PathVariable("questionId") Long questionId,
                           Model model) {
        Question question = questionService.findById(questionId);
        Set<Tag> tags = question.getTags();
        StringBuilder tagNamesBuilder = new StringBuilder();
        for (Tag tag : tags) {
            tagNamesBuilder.append(tag.getName()).append(" ");
        }
        String tagNames = tagNamesBuilder.toString().trim();
        model.addAttribute("question", question);
        model.addAttribute("tagNames", tagNames);
        return "question/edit-question";
    }

    @DeleteMapping("/question/delete/{deleteId}")
    public String delete(@PathVariable("deleteId") Long deleteId) {
        questionService.deleteById(deleteId);
        return "redirect:/questions";
    }

    @GetMapping("/search")
    public String searchAll(Model model,
                            @RequestParam(value = "tab", defaultValue = "new") String tab,
                            @RequestParam(value = "q", required = false) String q,
                            @RequestParam(defaultValue = "1") int page) {
        Page<Question> questionPage = null;
        Pageable pageable = PageRequest.of(page - 1, 6);

        String[] searchArray = q.split("[,\\s]+");

        List<Question> questions = new ArrayList<>();
        if (searchArray.length > 1) {
            if (searchArray[0].startsWith("user:")) {
                Long userId = Long.parseLong(searchArray[0].substring(5, searchArray[0].length()));

                String tagName = searchArray[1].startsWith("[")
                        ? searchArray[1].substring(1, searchArray[1].length() - 1)
                        : searchArray[1].substring(0, searchArray[1].length() - 1);
                questions = questionService.findQuestionsByUserAndTag(userId, tagName);
                model.addAttribute("questions", questions);
                model.addAttribute("q", q);
                return "all-question";
            }
            for (String search : searchArray) {
                if (tagService.tagExists(search)) {
                    List<Question> tagQuestions = tagService.findQuestionsByTagName(search);
                    questions.addAll(tagQuestions);
                } else {
                    List<Question> keywordQuestions = questionService.findQuestionsBySearch(search);
                    questions.addAll(keywordQuestions);
                }
            }
            model.addAttribute("questions", questions);
            model.addAttribute("q", q);
        } else {
            if (tagService.tagExists(searchArray[0])) {
                questions = tagService.findQuestionsByTagName(searchArray[0]);
                model.addAttribute("questions", questions);
                model.addAttribute("tagName", searchArray[0]);
                model.addAttribute("q", searchArray[0]);
            } else if (searchArray[0].endsWith("\"")) {
                String searchTerm = searchArray[0].startsWith("\"")
                        ? searchArray[0].substring(1, searchArray[0].length() - 1)
                        : searchArray[0].substring(0, searchArray[0].length() - 1);
                questions = questionService.findQuestionsBySearch(searchTerm);
                model.addAttribute("questions", questions);
                model.addAttribute("q", searchArray[0]);
            } else if (!(searchArray[0].endsWith("\"")) && !(searchArray[0].endsWith("]")) && !(searchArray[0].startsWith("user:"))) {
                questions = questionService.findQuestionsBySearch(searchArray[0]);
                model.addAttribute("questions", questions);
                model.addAttribute("q", searchArray[0]);
            } else if (searchArray[0].endsWith("]")) {
                String tagName = searchArray[0].startsWith("[")
                        ? searchArray[0].substring(1, searchArray[0].length() - 1)
                        : searchArray[0].substring(0, searchArray[0].length() - 1);
                questions = tagService.findQuestionsByTagName(tagName);
                model.addAttribute("questions", questions);
                model.addAttribute("tagName", tagName);
                model.addAttribute("q", searchArray[0]);
            } else if (searchArray[0].startsWith("user:")) {
                if (searchArray[0].length() > 5) {
                    int userId = Integer.parseInt(searchArray[0].substring(5, searchArray[0].length()));
                    User user = userService.findByUserId(userId);
                    if (user != null) {
                        questions = questionService.findByUser(user);
                        model.addAttribute("questions", questions);
                        model.addAttribute("q", searchArray[0]);
                    } else {
                        questions = questionService.findAll();
                        questionPage = questionService.searchAndSortByNewOrUnansweredOrScore(questions, tab, pageable);
                        model.addAttribute("questions", questions);
                        model.addAttribute("q", searchArray[0]);
                    }
                } else {
                    questions = questionService.findAll();
                    questionPage = questionService.searchAndSortByNewOrUnansweredOrScore(questions, tab, pageable);
                    model.addAttribute("questions", questions);
                    model.addAttribute("q", searchArray[0]);
                }

            }
        }
        questionPage = questionService.searchAndSortByNewOrUnansweredOrScore(questions, tab, pageable);

        for (Question question : questionPage) {
            question.setVerifiedCount(questionService.findQuestionWithVerifiedAnswerCount(question));
        }
        model.addAttribute("tab", tab);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", questionPage.getTotalPages());
        model.addAttribute("questions", questionPage);
        model.addAttribute("questionsSize", questions.size());

        return "all-question";
    }

    @GetMapping("/questions/user/{userId}/tagged/{tagName}")
    public String userWithTagged(@PathVariable("userId") Long id,
                                 @PathVariable("tagName") String tagName,
                                 Model model) {

        List<Question> questions = questionService.findQuestionsByUserAndTag(id, tagName);

        model.addAttribute("q", "user:" + id + " [" + tagName + "]");
        model.addAttribute("questions", questions);
        return "all-question";
    }

    @PostMapping("/question/upvote/{questionId}/{voteType}")
    public String upvote(@PathVariable Long questionId, @PathVariable VoteType voteType, Model model) {
        Question question = questionService.findById(questionId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(authentication.getName());
        Vote existingVote = questionService.findVoteByUserAndQuestion(user, question);


        if (existingVote != null) {

            if (existingVote.getVoteType() == voteType.NOVOTE) {
                existingVote.setVoteType(voteType);


                Integer voteCount = question.getVoteCount();

                question.setVoteCount(voteCount + 1);

            } else if (existingVote.getVoteType() == voteType.DOWNVOTE) {


                existingVote.setVoteType(voteType.UPVOTE);
                Integer voteCount = question.getVoteCount();
                question.setVoteCount(voteCount + 2);

            } else {
                existingVote.setVoteType(voteType.NOVOTE);
                Integer voteCount = question.getVoteCount();
                question.setVoteCount(voteCount - 1);
            }
        } else {

            Vote vote = new Vote();
            vote.setQuestion(question);
            vote.setUser(user);
            vote.setVoteType(voteType);
            questionService.createVote(vote);
            Integer voteCount = question.getVoteCount();
            question.setVoteCount(voteCount + 1);

        }
        model.addAttribute("voteType", voteType);

        questionService.save(question);

        return "redirect:/question/{questionId}";
    }

    @PostMapping("/question/downvote/{questionId}/{voteType}")
    public String downVote(@PathVariable Long questionId, @PathVariable VoteType voteType, Model model) {
        Question question = questionService.findById(questionId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(authentication.getName());

        Vote existingVote = questionService.findVoteByUserAndQuestion(user, question);

        if (existingVote != null) {
            if (existingVote.getVoteType() == voteType.NOVOTE) {

                existingVote.setVoteType(voteType);

                int voteCount = question.getVoteCount();
                question.setVoteCount(voteCount - 1);

            } else if (existingVote.getVoteType() == voteType.UPVOTE) {
                existingVote.setVoteType(voteType.DOWNVOTE);
                Integer voteCount = question.getVoteCount();
                question.setVoteCount(voteCount - 2);

            } else {
                existingVote.setVoteType(voteType.NOVOTE);
                Integer voteCount = question.getVoteCount();
                question.setVoteCount(voteCount + 1);
            }

        } else {
            Vote vote = new Vote();
            vote.setQuestion(question);
            vote.setUser(user);
            vote.setVoteType(voteType);
            questionService.createVote(vote);

            int voteCount = question.getVoteCount();
            question.setVoteCount(voteCount - 1);

        }
        model.addAttribute("voteType", voteType);
        questionService.save(question);

        return "redirect:/question/{questionId}";
    }
}