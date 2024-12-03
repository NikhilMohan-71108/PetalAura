package com.petalaura.library.Service;

import com.petalaura.library.dto.AdminDto;
import com.petalaura.library.model.Admin;

public interface AdminService {

    Admin save(AdminDto adminDto);

    Admin findByUsername(String username);
}
