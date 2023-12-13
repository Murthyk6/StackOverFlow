package com.stackoverflow.clone.service;

import com.stackoverflow.clone.entity.Question;
import com.stackoverflow.clone.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface TagService {
    Set<Tag> findOrCreateTag(String tagInput);

    void deleteTagIfNotUsed(Tag tag);

    List<Question> findQuestionsByTagName(String tagName);

    Page<Tag> findAll(Pageable pageable);

    Page<Tag> findAllByCreatedAtDesc(Pageable pageable);

    Page<Tag> findAllByTagNameAsc(Pageable pageable);

    Page<Tag> search(String search, String tab, Pageable pageable);

    boolean tagExists(String search);
    Page<Object[]> findTagsWithQuestionCountsOrderByCountDesc(Pageable pageable);
}
