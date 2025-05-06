package com.example.GameWWW.controllers.web;

import com.example.GameWWW.model.dto.request.TeamInfoReq;
import com.example.GameWWW.model.dto.request.UserReq;
import com.example.GameWWW.model.dto.responce.UserResp;
import com.example.GameWWW.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final UserService userService;

    @GetMapping(value = {"/", "/home"})
    public String getHome(){
        return "home";
    }

    @GetMapping("/main")
    public String getMain(){
        return "total/main";
    }

    @GetMapping( "/admin")
    public String getAdmin(){
        return "total/admin";
    }

    @GetMapping(value = "/user")
    public String getUser(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("user", authentication);
        return "total/user";
    }

    @GetMapping("/registration")
    public String login(Model model){
        model.addAttribute("userForm", new UserReq());
        model.addAttribute("team", new TeamInfoReq());
        return "registration";
    }

    @PostMapping("/saveUser")
    public String saveUser(UserReq reqUser, TeamInfoReq reqTeam){
        userService.addUser(reqUser,reqTeam);
        return "total/successful";
    }

    @GetMapping("/allUsers")
    public String getAllUsers (Model model){
        List<UserResp> allUsers = userService.getAllUsers();
            model.addAttribute("allUsers", allUsers);
            return "total/allUsers";

    }
    @GetMapping("/allUsers/delete/{id}")
    public String deleteTeam(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/allUsers";
    }


}
