package com.example.plevent.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration; // Ensure this is not conflicting
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Keep the import
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // Recommended for @PreAuthorize

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Enable method-level security checks like @PreAuthorize
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    // Authentication Manager
    @Bean
    public AuthenticationManager authenticationManager(
            HttpSecurity http, // Keep HttpSecurity for the shared object builder
            BCryptPasswordEncoder passwordEncoder // 🎯 FIX 1: Accept the globally defined encoder
    ) throws Exception {

        // This is the correct way to build the AuthenticationManager in modern Spring Security:
        AuthenticationManagerBuilder auth = http.getSharedObject(AuthenticationManagerBuilder.class);

        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder); // Use the injected encoder

        return auth.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/register", "/css/**", "/js/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/organizer/**").hasRole("ORGANIZER")
                        .requestMatchers("/user/**").hasRole("USER")
                        // 🎯 FIX 2: Ensure the dashboard page for general users is accessible
                        .requestMatchers("/").permitAll() // Allow general access to home page
                        .requestMatchers("/default").permitAll() // Or wherever /default redirects initially
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        // 🎯 FIX 3: Update defaultSuccessUrl logic
                        .successHandler((request, response, authentication) -> {
                            if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                                response.sendRedirect("/admin/dashboard");
                            } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ORGANIZER"))) {
                                response.sendRedirect("/organizer/dashboard");
                            } else {
                                response.sendRedirect("/user/dashboard");
                            }
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }
}