package com.mdev.messanger.client.controller;

import com.mdev.messanger.client.dto.UserDTO;
import com.mdev.messanger.client.model.User;
import com.mdev.messanger.client.repository.UserRepository;
import com.mdev.messanger.client.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

   /* @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable String id){
        String[] compoundUserId = id.split("#");
        String username = compoundUserId[0];
        String tag = compoundUserId[1];

        User user = userRepository.findByUsernameAndTag(username, id);
        UserDTO userDTO = new UserDTO(user.getUsername(), user.getTag(), user.get);
        return
    }*/

}
