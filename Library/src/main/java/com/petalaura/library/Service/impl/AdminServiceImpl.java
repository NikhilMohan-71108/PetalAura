package com.petalaura.library.Service.impl;

import com.petalaura.library.Repository.AdminRepository;
import com.petalaura.library.Repository.RoleRepository;
import com.petalaura.library.Service.AdminService;
import com.petalaura.library.dto.AdminDto;
import com.petalaura.library.model.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private RoleRepository roleRepository;




   @Override
    public Admin save(AdminDto adminDto) {
        Admin admin = new Admin();
        admin.setFirstName(adminDto.getFirstName());
        admin.setLastName(adminDto.getLastName());
        admin.setUsername(adminDto.getUsername());
        admin.setPassword(adminDto.getPassword());
        admin.setRoles(Arrays.asList(roleRepository.findByName("ADMIN")));
        return adminRepository.save(admin);
    }

    @Override
    public Admin findByUsername(String username) {

        return adminRepository.findByUsername(username);
    }
}
