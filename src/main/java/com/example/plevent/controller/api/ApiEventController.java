package com.example.plevent.controller.api;

import com.example.plevent.model.Booking;
import com.example.plevent.model.Event;
import com.example.plevent.model.User;
import com.example.plevent.service.BookingService;
import com.example.plevent.service.EventService;
import com.example.plevent.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class ApiEventController {

    private final EventService eventService;
    private final BookingService bookingService;
    private final UserService userService;

    public ApiEventController(EventService eventService, BookingService bookingService, UserService userService) {
        this.eventService = eventService;
        this.bookingService = bookingService;
        this.userService = userService;
    }

    // Get all approved events
    @GetMapping
    public List<Event> getAllApprovedEvents() {
        return eventService.getByStatus(com.example.plevent.model.EventStatus.APPROVED);
    }

    // Get event by id
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEvent(@PathVariable Integer id) {
        Event event = eventService.findById(id);
        if (event == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(event);
    }

    // Book an event
    @PostMapping("/book/{id}")
    public ResponseEntity<String> bookEvent(@PathVariable Integer id, Principal principal) {
        Event event = eventService.findById(id);
        if (event == null) return ResponseEntity.badRequest().body("Event not found");
        if (event.getCapacity() <= 0) return ResponseEntity.badRequest().body("Event full");

        User user = userService.findByEmail(principal.getName());
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setEvent(event);
        bookingService.save(booking);

        // decrease capacity
        event.setCapacity(event.getCapacity() - 1);
        eventService.save(event);

        return ResponseEntity.ok("Booking successful");
    }
}
