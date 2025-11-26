package com.example.plevent.service;

import com.example.plevent.model.Role;
import com.example.plevent.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    // 🎯 RENAMED METHOD: Consistent with JpaRepository
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    public Role findByName(String name) {
        return roleRepository.findByName(name);
    }

    // Optionally keep getAllRoles() and just call findAll() internally
    /*
    public List<Role> getAllRoles() {
        return this.findAll();
    }
    */
}