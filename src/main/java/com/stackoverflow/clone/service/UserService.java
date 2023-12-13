package com.stackoverflow.clone.service;

import com.stackoverflow.clone.entity.Question;
import com.stackoverflow.clone.entity.Tag;
import com.stackoverflow.clone.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDetails loadUserByUsername(String username);
    User findByUsername(String username);

    List<User> findAll();
    List<Object[]> findTop3TagsForEachUser();

    List<Tag> findTop3TagsByUserId(Long userId);

    List<Tag> findTopTags(Long userId);

    Optional<User> findById(Long id);

    User findByUserId(int userId);

    List<User> search(String searchTerm);

    Page<User> searchAndSortByUsernameOrName(String searchTerm, String tab, Pageable pageable);

    User findById(int userId);
    void save(User user);
    boolean existsByUsername(String username);
}
