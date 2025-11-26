package com.example.plevent.controller.api;

import com.example.plevent.model.Booking;
import com.example.plevent.model.User;
import com.example.plevent.service.BookingService;
import com.example.plevent.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class ApiBookingController {

    private final BookingService bookingService;
    private final UserService userService;

    public ApiBookingController(BookingService bookingService, UserService userService) {
        this.bookingService = bookingService;
        this.userService = userService;
    }

    // Get bookings of current user
    @GetMapping
    public ResponseEntity<List<Booking>> getMyBookings(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        if (user == null) return ResponseEntity.badRequest().build();
        List<Booking> bookings = bookingService.getByUser(user.getId());
        return ResponseEntity.ok(bookings);
    }

    // Cancel booking
    @PostMapping("/cancel/{id}")
    public ResponseEntity<String> cancelBooking(@PathVariable Integer id) {
        bookingService.cancel(id);
        return ResponseEntity.ok("Booking cancelled");
    }
}
