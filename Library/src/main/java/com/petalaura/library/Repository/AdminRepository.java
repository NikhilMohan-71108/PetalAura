package com.petalaura.library.Repository;

import com.petalaura.library.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
Admin findByUsername(String username);

}
