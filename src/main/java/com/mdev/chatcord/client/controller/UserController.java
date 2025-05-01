package com.mdev.chatcord.client.controller;

import com.mdev.chatcord.client.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
