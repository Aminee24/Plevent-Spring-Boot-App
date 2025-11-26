package com.example.plevent.controller;

import com.example.plevent.model.Event;
import com.example.plevent.model.User;
import com.example.plevent.service.EventService;
import com.example.plevent.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.plevent.service.CategoryService;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/organizer")
@PreAuthorize("hasRole('ORGANIZER')")
public class OrganizerController {

    private final EventService eventService;
    private final UserService userService;
    private final CategoryService categoryService; // 🎯 NEW SERVICE

    public OrganizerController(EventService eventService, UserService userService, CategoryService categoryService) {
        this.eventService = eventService;
        this.userService = userService;
        this.categoryService = categoryService; // Assign new service
    }

    // --- Dashboard and Event List (Corrected) ---
    @GetMapping("/dashboard")
    public String organizerDashboard(Model model, Principal principal) {
        String email = principal.getName();
        User organizer = userService.findByEmail(email);

        if (organizer == null) {
            throw new IllegalStateException("Organizer not found in database: " + email);
        }

        List<Event> myEvents = eventService.getByOrganizer(organizer.getId());

        model.addAttribute("organizer", organizer);
        model.addAttribute("myEvents", myEvents);

        return "organizer/dashboard";
    }

    // --- New Event Form (FIXED) ---
    @GetMapping("/event/new")
    public String newEventForm(Model model, Principal principal) {

        // 🎯 FIX: Fetch the organizer to ensure the template has access to the organizer's ID
        // We need this ID to safely link the event on the POST request.
        String email = principal.getName();
        User organizer = userService.findByEmail(email);

        if (organizer == null) {
            throw new IllegalStateException("Organizer not found for new event form: " + email);
        }
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("event", new Event());
        model.addAttribute("organizerId", organizer.getId()); // Pass the ID to the form

        return "organizer/new_event";
    }

    // --- Save Event (Corrected, relies on the new form data) ---
    @PostMapping("/event/save")
    public String saveEvent(@ModelAttribute Event event, Principal principal) {

        // The safest way is to fetch the full object again, even if the ID was passed.
        String email = principal.getName();
        User organizer = userService.findByEmail(email);

        if (organizer == null) {
            throw new IllegalStateException("Organizer not found during event save: " + email);
        }

        event.setOrganizer(organizer);
        eventService.save(event);

        return "redirect:/organizer/dashboard";
    }

    // 1. ✏️ EDIT EVENT FORM (GET)
    @GetMapping("/event/edit/{id}")
    public String editEventForm(@PathVariable Integer id, Model model, Principal principal) {
        // Fetch the existing event
        Event event = eventService.findById(id);

        // 🎯 Security Check: Ensure the logged-in user is the event organizer
        User loggedInOrganizer = userService.findByEmail(principal.getName());

        if (event == null || !event.getOrganizer().getId().equals(loggedInOrganizer.getId())) {
            // Event not found or user is not the owner
            return "redirect:/organizer/dashboard?error=unauthorized";
        }

        model.addAttribute("event", event);

        // If you had categories, you would also add them here for the dropdown
        // model.addAttribute("categories", categoryService.findAll());

        return "organizer/edit_event"; // Create this new Thymeleaf template
    }

    // 2. 💾 UPDATE EVENT (POST)
    @PostMapping("/event/update/{id}")
    public String updateEvent(@PathVariable Integer id, @ModelAttribute Event updatedEvent, Principal principal) {
        // Fetch the existing event from the database
        Event existingEvent = eventService.findById(id);

        // 🎯 Security Check (Repeat): Ensure the user owns the event
        User loggedInOrganizer = userService.findByEmail(principal.getName());

        if (existingEvent != null && existingEvent.getOrganizer().getId().equals(loggedInOrganizer.getId())) {

            // Update fields from the form submission to the existing event
            existingEvent.setTitle(updatedEvent.getTitle());
            existingEvent.setDescription(updatedEvent.getDescription());
            existingEvent.setEventDate(updatedEvent.getEventDate());
            existingEvent.setLocation(updatedEvent.getLocation());
            existingEvent.setCapacity(updatedEvent.getCapacity());
            // Note: Status and Organizer should not be changed by the organizer form

            eventService.save(existingEvent); // Save the updated entity
        }

        return "redirect:/organizer/dashboard";
    }

    // 3. 🗑️ DELETE EVENT (GET or POST)
    // Using GET here for simplicity, but POST is recommended for actual deletion
    @GetMapping("/event/delete/{id}")
    public String deleteEvent(@PathVariable Integer id, Principal principal) {
        Event event = eventService.findById(id);

        // 🎯 Security Check (Repeat): Ensure the user owns the event
        User loggedInOrganizer = userService.findByEmail(principal.getName());

        if (event != null && event.getOrganizer().getId().equals(loggedInOrganizer.getId())) {
            eventService.deleteById(id);
        }

        return "redirect:/organizer/dashboard";
    }
}
