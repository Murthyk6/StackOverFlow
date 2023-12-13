package com.stackoverflow.clone.service.implementation;

import com.stackoverflow.clone.entity.Question;
import com.stackoverflow.clone.entity.Tag;
import com.stackoverflow.clone.repository.TagRepository;
import com.stackoverflow.clone.service.TagService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class TagServiceImpl implements TagService {

    private TagRepository tagRepository;

    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public Set<Tag> findOrCreateTag(String tagInput) {
        String[] tagNames = tagInput.split(" ");
        Set<Tag> tags = new HashSet<>();
        Tag tag;
        for (String tagName : tagNames) {
            tagName = tagName.trim();
            if (!tagName.isEmpty()) {
                Optional<Tag> existingTag = tagRepository.findByName(tagName);
                if (existingTag.isPresent()) {
                    tag = existingTag.get();
                } else {
                    tag = new Tag();
                    tag.setName(tagName.toLowerCase());
                    tagRepository.save(tag);
                }
                tags.add(tag);
            }
        }
        return tags;
    }

    @Override
    public void deleteTagIfNotUsed(Tag tag) {
        if (!tag.getPosts().isEmpty()) {
            return;
        }
        tagRepository.delete(tag);
    }

    @Override
    public List<Question> findQuestionsByTagName(String tagName) {
        return tagRepository.findQuestionsByTagId(tagName);
    }

    @Override
    public Page<Tag> findAll(Pageable pageable) {
        return tagRepository.findAll(pageable);
    }

    @Override
    public Page<Tag> findAllByCreatedAtDesc(Pageable pageable) {
        return tagRepository.findAllTagsByCreatedAtDesc(pageable);
    }

    @Override
    public Page<Tag> findAllByTagNameAsc(Pageable pageable) {
        return tagRepository.findAllByOrderByNameAsc(pageable);
    }

    @Override
    public Page<Tag> search(String search, String tab, Pageable pageable) {

        if (tab != null && !tab.equals("popular")) {
            int sortBy = tab.equals("name") ? 1 : 0;
            Sort sort = sortBy == 1 ? Sort.by(Sort.Order.asc("name")) : Sort.by(Sort.Order.desc("createdAt"));

            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
            return tagRepository.searchAndSort(search,pageable);
        }
        return tagRepository.search(search,pageable);
    }

    @Override
    public boolean tagExists(String search) {
        return tagRepository.searchIfExists(search);
    }

    @Override
    public Page<Object[]> findTagsWithQuestionCountsOrderByCountDesc(Pageable pageable) {
        return tagRepository.findTagsWithQuestionCountsOrderByCountDesc(pageable);
    }

}