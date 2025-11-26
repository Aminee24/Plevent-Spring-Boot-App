package com.example.plevent.service;

import com.example.plevent.model.Event;
import com.example.plevent.model.EventStatus;
import com.example.plevent.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Event save(Event event) {
        return eventRepository.save(event);
    }

    public List<Event> getAll() {
        return eventRepository.findAll();
    }

    public List<Event> getByStatus(EventStatus status) {
        return eventRepository.findByStatus(status);
    }

    public List<Event> getByOrganizer(Integer organizerId) {
        return eventRepository.findByOrganizerId(organizerId);
    }

    public Event findById(Integer id) {
        return eventRepository.findById(id).orElse(null);
    }

    public void deleteById(Integer id) {
        eventRepository.deleteById(id);
    }
    public void deleteByOrganizerId(Integer organizerId) {
        eventRepository.deleteByOrganizerId(organizerId);
    }
}
