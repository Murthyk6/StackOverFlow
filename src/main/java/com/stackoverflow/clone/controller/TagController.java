package com.stackoverflow.clone.controller;

import com.stackoverflow.clone.entity.Question;
import com.stackoverflow.clone.entity.Tag;
import com.stackoverflow.clone.service.QuestionService;
import com.stackoverflow.clone.service.TagService;
import com.stackoverflow.clone.util.TimeElapsedFormatter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
public class TagController {

    private QuestionService questionService;
    private TagService tagService;

    public TagController(TagService tagService, QuestionService questionService) {
        this.tagService = tagService;
        this.questionService = questionService;
    }

    @GetMapping("/tags")
    public String listTags(Model model,
                           @RequestParam(defaultValue = "1") int page,
                           @RequestParam(value = "tagSearch", defaultValue = "") String tagSearch,
                           @RequestParam(value = "tab", defaultValue = "popular") String tab) {

        int pageSize = 10;
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<Tag> tags;
        Map<Tag, Integer> questionsCountByTag = new LinkedHashMap<>();

        if (tagSearch != null && !tagSearch.equals("")) {
            tags = tagService.search(tagSearch, tab, pageable);
        } else if (tab == null) {
            tags = tagService.findAll(pageable);
        } else if (tab.equals("name")) {
            tags = tagService.findAllByTagNameAsc(pageable);
        } else {
            tags = tagService.findAllByCreatedAtDesc(pageable);
        }

        for (Tag tag : tags) {
            int count = questionService.countQuestionsByTag(tag);
            questionsCountByTag.put(tag, count);
        }

        Map<Tag, String> formattedCreatedAt = new HashMap<>();

        if (tab != null && tab.equals("popular")) {

            Page<Object[]> popularTags = tagService.findTagsWithQuestionCountsOrderByCountDesc(pageable);
            for (Object[] tagWithCount : popularTags) {

                Tag tag = (Tag) tagWithCount[0];
                Date createdAt = tag.getCreatedAt();
                String formattedTimeElapsed = TimeElapsedFormatter.formatTimeElapsed(createdAt);
                formattedCreatedAt.put(tag, formattedTimeElapsed);
            }

            model.addAttribute("popularTags", popularTags);
        }

        else {

            for (Tag tag : questionsCountByTag.keySet()) {
                Date createdAt = tag.getCreatedAt();
                String formattedTimeElapsed = TimeElapsedFormatter.formatTimeElapsed(createdAt);
                formattedCreatedAt.put(tag, formattedTimeElapsed);
            }
        }

        if (tagSearch != null && !tagSearch.equals("")) {
            model.addAttribute("tagSearch", tagSearch);
        }
        if (tab != null && !tab.equals("")) {
            model.addAttribute("tab", tab);
        }
        model.addAttribute("totalPages", tags.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("formattedCreatedAt", formattedCreatedAt);
        model.addAttribute("questionsCountByTag", questionsCountByTag);

        return "tag/list-tag";
    }

    @GetMapping("questions/tagged/{tagName}")
    public String listQuestionsByTags(Model model,
                                      @PathVariable("tagName") String tagName) {
        List<Question> questions = tagService.findQuestionsByTagName(tagName);
        model.addAttribute("questions", questions);
        model.addAttribute("tagName", tagName);
        model.addAttribute("q", "[" + tagName + "]");
        return "all-question";
    }
}
