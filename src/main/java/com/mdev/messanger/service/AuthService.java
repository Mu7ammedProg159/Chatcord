package com.mdev.messanger.service;

import com.mdev.messanger.model.User;
import com.mdev.messanger.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtService jwtService;

    public String signIn(String username, String key){
        User user = userRepository.findByUsername(username);
        if (user != null && passwordEncoder.matches(key, user.getPassword()))
            return jwtService.generateToken(username);
        return null;
    }

    public boolean isUserRegistered(String username){
        return userRepository.findByUsername(username) != null;
    }

    public void signUp(String username, String key){
        try{
            User user = new User();

            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(key));
            userRepository.save(user);
        } catch (Exception e){
            System.out.println("User already registered with this username.");
        }
    }
}
