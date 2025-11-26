package com.example.plevent.controller;

import com.example.plevent.model.Role; // Assuming this model exists
import com.example.plevent.model.User; // Assuming this model exists
import com.example.plevent.service.RoleService; // Service dependency
import com.example.plevent.service.UserService; // Service dependency
import org.springframework.security.core.Authentication; // 🎯 Required for checking login authority
import org.springframework.security.core.GrantedAuthority; // Required for iterating authorities
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // 🎯 Required for adding data to the view (register form)
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collection;
import java.util.Optional;

@Controller
public class AuthController {

    // --- Dependencies ---
    private final UserService userService;
    private final RoleService roleService;

    // Constructor Injection: Spring automatically provides these beans
    public AuthController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // Redirect users based on role
    @GetMapping("/default")
    public String defaultAfterLogin(org.springframework.security.core.Authentication auth) {
        String role = auth.getAuthorities().iterator().next().getAuthority();
        switch (role) {
            case "ROLE_ADMIN": return "redirect:/admin/dashboard";
            case "ROLE_ORGANIZER": return "redirect:/organizer/dashboard";
            case "ROLE_USER": return "redirect:/user/dashboard";
            default: return "redirect:/login";
        }
    }
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user) {
        // set default role
        Role role = roleService.findByName("ROLE_USER");
        user.setRole(role);
        userService.saveUser(user);
        return "redirect:/login";
    }

}
