package com.mdev.messanger.client.service;

import com.mdev.messanger.client.controller.UserController;
import com.mdev.messanger.client.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserController userController;
    private final UserRepository userRepository;



}
