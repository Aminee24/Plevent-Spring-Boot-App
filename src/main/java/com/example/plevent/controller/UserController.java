package com.example.plevent.controller;

import com.example.plevent.model.Booking;
import com.example.plevent.model.Event;
import com.example.plevent.model.User;
import com.example.plevent.service.BookingService;
import com.example.plevent.service.EventService;
import com.example.plevent.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    private final EventService eventService;
    private final BookingService bookingService;
    private final UserService userService;

    public UserController(EventService eventService, BookingService bookingService, UserService userService) {
        this.eventService = eventService;
        this.bookingService = bookingService;
        this.userService = userService;
    }

    // User dashboard - list approved events
    @GetMapping("/dashboard")
    public String userDashboard(Model model) {
        List<Event> events = eventService.getByStatus(com.example.plevent.model.EventStatus.APPROVED);
        model.addAttribute("events", events);
        return "user/dashboard";
    }

    // Event details page
    @GetMapping("/event/{id}")
    public String eventDetails(@PathVariable Integer id, Model model) {
        Event event = eventService.findById(id);
        model.addAttribute("event", event);
        return "user/event_details";
    }

    // Book event
    @PostMapping("/event/book/{id}")
    public String bookEvent(@PathVariable Integer id, Principal principal) {
        Event event = eventService.findById(id);
        if (event != null && event.getCapacity() > 0) {
            User user = userService.findByEmail(principal.getName());
            Booking booking = new Booking();
            booking.setUser(user);
            booking.setEvent(event);
            bookingService.save(booking);

            // reduce event capacity
            event.setCapacity(event.getCapacity() - 1);
            eventService.save(event);
        }
        return "redirect:/user/dashboard";
    }

    // View my bookings
    @GetMapping("/bookings")
    public String myBookings(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        List<Booking> bookings = bookingService.getByUser(user.getId());
        model.addAttribute("bookings", bookings);
        return "user/my_bookings";
    }

    // Cancel booking
    @PostMapping("/booking/cancel/{id}")
    public String cancelBooking(@PathVariable Integer id) {

        // 1. Fetch the booking we are about to cancel
        Booking booking = bookingService.findById(id);

        if (booking != null) {

            // 2. Get the associated event
            Event event = booking.getEvent();

            // 3. Delete the booking record
            bookingService.delete(id);

            // 4. Increase event capacity and save
            if (event != null) {
                event.setCapacity(event.getCapacity() + 1);
                eventService.save(event);
            }
        }

        return "redirect:/user/bookings";
    }
}
