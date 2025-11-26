package com.example.plevent.repository;

import com.example.plevent.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByUserId(Integer userId);
    List<Booking> findByEventId(Integer eventId);
    @Transactional
    void deleteByUserId(Integer userId);
}
