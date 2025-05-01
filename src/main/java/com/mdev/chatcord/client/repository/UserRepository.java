package com.mdev.chatcord.client.repository;

import com.mdev.chatcord.client.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    //User findByUsername(String username);
    User findByEmail(String email);
    User findByUsernameAndTag(String username, String tag);
    boolean existsByTag(String tag);
}
