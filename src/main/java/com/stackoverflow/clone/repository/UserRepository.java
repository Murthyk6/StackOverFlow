package com.stackoverflow.clone.repository;


import com.stackoverflow.clone.entity.Question;
import com.stackoverflow.clone.entity.Tag;
import com.stackoverflow.clone.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    boolean existsByUsername(String username);

    @Query(value = "SELECT u.id, u.username, t.id, COUNT(t.id) AS tag_count " +
            "FROM users u " +
            "LEFT JOIN questions q ON u.id = q.user_id " +
            "LEFT JOIN question_tags qt ON q.id = qt.question_id " +
            "LEFT JOIN tags t ON qt.tag_id = t.id " +
            "GROUP BY u.id, t.id " +
            "ORDER BY u.id, tag_count DESC " +
            "LIMIT 3",
            nativeQuery = true)
    List<Object[]> findTop3TagsForEachUser();

    @Query("SELECT t FROM User u " +
            "JOIN u.questions q " +
            "JOIN q.tags t " +
            "WHERE u.id = :userId " +
            "GROUP BY t " +
            "ORDER BY COUNT(t) DESC " +
            "LIMIT :limit")
    List<Tag> findTop3TagsByUserId(@Param("userId") Long userId,
                                   @Param("limit") int limit);

    @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE " +
            "LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(u.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<User> searchByUsernameOrNameIgnoreCase(@Param("searchTerm") String searchTerm);


    @Query("SELECT u FROM User u JOIN FETCH u.questions q GROUP BY u.id ORDER BY COUNT(q) DESC")
    Page<User> findAllByOrderByNoOfQuestionsDesc(Pageable pageable);

    // Sort users by their creation date (newest first)
    Page<User> findAllByOrderByCreatedAtDesc(Pageable pageable);

//    @Query("SELECT u FROM User u JOIN FETCH u.questions q " +
//            "WHERE (:isEmpty = 1) OR LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(u.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
//            "GROUP BY u.id " +
//            "ORDER BY COUNT(q) DESC")
//    Page<User> searchAndSort(@Param("searchTerm") String searchTerm,
//                                             @Param("isEmpty") int isEmpty,
//                                             Pageable pageable);

    @Query("SELECT u FROM User u WHERE ((:isEmpty = 1) OR LOWER(u.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(u.username) LIKE" +
            " LOWER(CONCAT('%', :searchTerm, '%'))) ORDER BY u.username ASC")
    Page<User> searchAndSort(@Param("searchTerm") String searchTerm, int isEmpty, Pageable pageable);

    @Query("SELECT u FROM User u " +
            "WHERE ((:isEmpty = 1) OR (LOWER(u.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')))) " +
            "ORDER BY u.createdAt DESC")
    Page<User> searchAndSortCreatedAt(@Param("searchTerm") String searchTerm, int isEmpty, Pageable pageable);

    @Query("SELECT u FROM User u " +
            "WHERE ((:isEmpty = 1) OR (LOWER(u.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')))) " +
            "ORDER BY SIZE(u.questions) DESC")
    Page<User> searchAndSortNoOfQuestions(@Param("searchTerm") String searchTerm, int isEmpty, Pageable pageable);

}
