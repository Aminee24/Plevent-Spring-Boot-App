package com.example.plevent.repository;

import com.example.plevent.model.Event;
import com.example.plevent.model.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Integer> {
    List<Event> findByStatus(EventStatus status);
    List<Event> findByOrganizerId(Integer organizerId);
    // Inside com.example.plevent.repository.EventRepository.java
    @Transactional
    void deleteByOrganizerId(Integer organizerId);
}
