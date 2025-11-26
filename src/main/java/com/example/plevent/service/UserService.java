package com.example.plevent.service;

import com.example.plevent.model.User;
import com.example.plevent.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.context.annotation.Lazy; // Critical Import

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder; // 🎯 FIX 1: Declare the field
    // Inject dependent services (REQUIRED if handling cleanup in service)
    private final BookingService bookingService;
    private final EventService eventService;

    // 🎯 FIX 2: Updated Constructor to include passwordEncoder
    public UserService(UserRepository userRepository,
                       @Lazy BookingService bookingService, // Use @Lazy for potential circular dependency
                       @Lazy EventService eventService,      // Use @Lazy for potential circular dependency
                       BCryptPasswordEncoder passwordEncoder) { // Inject the encoder
        this.userRepository = userRepository;
        this.bookingService = bookingService;
        this.eventService = eventService;
        this.passwordEncoder = passwordEncoder; // Assign the encoder
    }

    // Used for login
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);

        if (user == null)
            throw new UsernameNotFoundException("User not found");

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().getName()))
        );
    }

    // Used for registration
    public void saveUser(User user) {
        // Only encode if the password is new/changed and not already encoded (common practice)
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userRepository.save(user);
    }

    // Used in ApiBookingController
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // 🎯 Used by AdminController to list all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 🎯 Used by AdminController to update a user's role
    public User findById(Integer id) {
        Optional<User> userOptional = userRepository.findById(id);
        return userOptional.orElse(null);
    }

    // 🎯 Robust delete method to handle dependencies
    public void deleteById(Integer id) {
        // 1. Delete all bookings associated with this user
        // Requires a new derived delete method in BookingRepository
        bookingService.deleteByUserId(id);

        // 2. Delete all events organized by this user
        // Requires a new derived delete method in EventRepository
        eventService.deleteByOrganizerId(id);

        // 3. Delete the user
        userRepository.deleteById(id);
    }
}