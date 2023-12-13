package com.stackoverflow.clone.controller;


import com.stackoverflow.clone.entity.User;
import com.stackoverflow.clone.repository.UserRepository;
import com.stackoverflow.clone.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "registration";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") @Valid User user,
                               BindingResult result, Model model) {

        if (result.hasErrors()) {
            return "registration";
        }


        if (userService.existsByUsername(user.getUsername())) {
//            result.rejectValue("username", "error.username", "Username is already taken");
            model.addAttribute("user", user);
            model.addAttribute("usernameError", "Username is already taken");
            return "registration";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        user.setRole("ROLE_USER");

        userService.save(user);

        return "redirect:/login";
    }


    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }
}
