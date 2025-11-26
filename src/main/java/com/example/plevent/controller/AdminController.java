package com.example.plevent.controller;

import com.example.plevent.model.Event;
import com.example.plevent.model.EventStatus;
import com.example.plevent.model.Role; // NEW IMPORT
import com.example.plevent.model.User; // NEW IMPORT
import com.example.plevent.service.EventService;
import com.example.plevent.service.RoleService; // NEW IMPORT
import com.example.plevent.service.UserService; // NEW IMPORT

import org.springframework.security.access.prepost.PreAuthorize; // NEW IMPORT
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')") // 🎯 SECURITY: Restrict access to only users with ROLE_ADMIN
public class AdminController {

    private final EventService eventService;
    private final UserService userService; // NEW DEPENDENCY
    private final RoleService roleService; // NEW DEPENDENCY

    // Constructor Injection (Updated to include all services)
    public AdminController(EventService eventService, UserService userService, RoleService roleService) {
        this.eventService = eventService;
        this.userService = userService;
        this.roleService = roleService;
    }

    // --- 1. ADMIN DASHBOARD (Event Management) ---

    // Maps to /admin/dashboard
    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        // Events: Show pending events for approval
        model.addAttribute("pendingEvents", eventService.getByStatus(EventStatus.PENDING));

        // Users: Get list of all users and roles for management section
        model.addAttribute("allUsers", userService.getAllUsers());
        model.addAttribute("allRoles", roleService.findAll());

        return "admin/dashboard"; // Maps to src/main/resources/templates/admin/dashboard.html
    }

    // Approve Event
    @PostMapping("/event/approve/{id}")
    public String approveEvent(@PathVariable Integer id) {
        Event event = eventService.findById(id);
        if (event != null) {
            event.setStatus(EventStatus.APPROVED);
            eventService.save(event);
        }
        return "redirect:/admin/dashboard";
    }

    // Refuse Event
    @PostMapping("/event/refuse/{id}")
    public String refuseEvent(@PathVariable Integer id) {
        Event event = eventService.findById(id);
        if (event != null) {
            event.setStatus(EventStatus.REFUSED);
            eventService.save(event);
        }
        return "redirect:/admin/dashboard";
    }

    // --- 2. USER ROLE MANAGEMENT ---

    // Endpoint to handle role update for any user
    @PostMapping("/user/updateRole/{userId}")
    public String updateRole(@PathVariable Integer userId, @RequestParam("roleName") String roleName) {
        User user = userService.findById(userId); // Assuming findById exists
        Role newRole = roleService.findByName(roleName); // Assuming findByName exists

        // Prevent admin from revoking their own role (optional security check)
        // Note: You may want to prevent changing the role of the currently logged-in admin user

        if (user != null && newRole != null) {
            user.setRole(newRole);
            userService.saveUser(user);
        }
        return "redirect:/admin/dashboard";
    }

    // --- 3. 🗑️ DELETE USER ---

    // --- 🗑️ DELETE USER ---
    @PostMapping("/user/delete/{userId}")
    public String deleteUser(@PathVariable Integer userId, Principal principal) {

        User userToDelete = userService.findById(userId);
        User currentAdmin = userService.findByEmail(principal.getName());

        // 🎯 FIX 1: Prevent self-deletion
        if (userToDelete != null && currentAdmin != null && userToDelete.getId().equals(currentAdmin.getId())) {
            return "redirect:/admin/dashboard?error=cannotDeleteSelf";
        }

        if (userToDelete != null) {
            // FIX 2: Call the delete service method.
            // The service (or the database constraints) must handle the cleanup
            // of related Bookings (user) and Events (organizer).
            userService.deleteById(userId);
        }

        return "redirect:/admin/dashboard?success=userDeleted";
    }

}