// src/main/java/com/example/plevent/config/DataInitializer.java

package com.example.plevent.config;
import com.example.plevent.model.Category; // NEW IMPORT
import com.example.plevent.model.Role;
import com.example.plevent.repository.CategoryRepository; // NEW IMPORT
import com.example.plevent.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {
            // Check if roles table is empty before populating
            if (roleRepository.count() == 0) {
                List<Role> roles = List.of(
                        new Role("ROLE_ADMIN"),
                        new Role("ROLE_ORGANIZER"),
                        new Role("ROLE_USER")
                );
                roleRepository.saveAll(roles);
                System.out.println("--- Roles Initialized ---");
            }
        };
    }
    @Bean
    public CommandLineRunner initCategories(CategoryRepository categoryRepository) { // 🎯 NEW BEAN METHOD
        return args -> {
            if (categoryRepository.count() == 0) {
                List<Category> categories = List.of(
                        // Assumes Category model has a constructor for (name, description)
                        new Category("Technology", "Events related to tech, coding, and startups."),
                        new Category("Music", "Concerts, festivals, and live performances."),
                        new Category("Sports", "Fitness events, tournaments, and outdoor activities."),
                        new Category("Art & Culture", "Museums, galleries, and theater shows.")
                );

                categoryRepository.saveAll(categories);
                System.out.println("--- Categories Initialized ---");
            }
        };
    }
}