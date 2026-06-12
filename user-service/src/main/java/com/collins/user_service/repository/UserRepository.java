package com.collins.user_service.repository;

import com.collins.user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Long, User> {
    boolean existsByEmail(String email);

    boolean existByEmail(String email);
}
