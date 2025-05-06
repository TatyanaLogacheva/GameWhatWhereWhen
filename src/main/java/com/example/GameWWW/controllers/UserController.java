package com.example.GameWWW.controllers;

import com.example.GameWWW.model.dto.request.TeamInfoReq;
import com.example.GameWWW.model.dto.request.UserReq;
import com.example.GameWWW.model.dto.responce.UserResp;
import com.example.GameWWW.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/addUser")
    public UserResp addUser (@RequestBody UserReq reqU, @RequestBody TeamInfoReq reqT){
        return userService.addUser(reqU,reqT);
    }

    @GetMapping ("/{id}")
    public UserResp getUser(@PathVariable Long id){
        return userService.getUser(id);
    }

    @PutMapping("/{id}")
    public UserResp updateUser (@PathVariable Long id, @RequestBody UserReq req){
       return userService.updateUser(id, req);
    }

    @DeleteMapping ("/{id}")
    public void deleteUser (@PathVariable Long id){
        userService.deleteUser(id);
    }

    @GetMapping
    public List<UserResp> getAllUsers (){
        return userService.getAllUsers();
    }
}
