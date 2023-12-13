package com.stackoverflow.clone.service.implementation;

import com.stackoverflow.clone.entity.Tag;
import com.stackoverflow.clone.entity.User;
import com.stackoverflow.clone.repository.UserRepository;
import com.stackoverflow.clone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService, UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                true,
                true, true, true,
                getAuthorities(user.getRole())
        );
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public List<Object[]> findTop3TagsForEachUser() {
        return userRepository.findTop3TagsForEachUser();
    }

    @Override
    public List<Tag> findTop3TagsByUserId(Long userId) {
        return userRepository.findTop3TagsByUserId(userId,3);
    }

    @Override
    public User findById(int userId) {
        return userRepository.findById(Long.valueOf(userId)).get();
    }

    @Override
    public User findByUserId(int userId) {
        Optional<User> userOptional = userRepository.findById(Long.valueOf(userId));
        return userOptional.orElse(null);
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public List<Tag> findTopTags(Long userId) {
        return userRepository.findTop3TagsByUserId(userId,5);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> search(String searchTerm) {
        return null;
    }

    @Override
    public Page<User> searchAndSortByUsernameOrName(String searchTerm, String tab, Pageable pageable) {
        int isEmpty = (searchTerm == null || searchTerm.isEmpty()) ? 1 : 0;

        if(tab.equals("name")){
            return userRepository.searchAndSort(searchTerm,isEmpty,pageable);
        }
        if(tab.equals("new")){
            return userRepository.searchAndSortCreatedAt(searchTerm,isEmpty,pageable);
        }
        return userRepository.searchAndSortNoOfQuestions(searchTerm,isEmpty,pageable);
    }


    private Collection<? extends GrantedAuthority> getAuthorities(String role) {
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

}
