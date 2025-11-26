package com.example.plevent.service;

import com.example.plevent.model.Booking;
import com.example.plevent.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public Booking save(Booking booking) {
        return bookingRepository.save(booking);
    }

    public List<Booking> getByUser(Integer userId) {
        return bookingRepository.findByUserId(userId);
    }

    public List<Booking> getByEvent(Integer eventId) {
        return bookingRepository.findByEventId(eventId);
    }

    // 🎯 FIX 1: Add findById for the UserController to retrieve the booking and the event details.
    public Booking findById(Integer id) {
        return bookingRepository.findById(id).orElse(null);
    }

    // This method updates status to CANCELLED (soft delete), useful if you need the history.
    public void cancel(Integer id) {
        Booking booking = bookingRepository.findById(id).orElse(null);
        if (booking != null) {
            booking.setStatus(com.example.plevent.model.BookingStatus.CANCELLED);
            bookingRepository.save(booking);
        }
    }

    // 🎯 FIX 2: Add a direct delete method (hard delete), which the UserController now needs.
    public void delete(Integer id) {
        bookingRepository.deleteById(id);
    }
    public void deleteByUserId(Integer userId) {
        bookingRepository.deleteByUserId(userId);
    }
}