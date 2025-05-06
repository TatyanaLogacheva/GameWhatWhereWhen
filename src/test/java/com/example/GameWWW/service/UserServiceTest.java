package com.example.GameWWW.service;

import com.example.GameWWW.model.db.entity.Team;
import com.example.GameWWW.model.db.entity.User;
import com.example.GameWWW.model.db.repository.UserRepository;
import com.example.GameWWW.model.dto.request.UserReq;
import com.example.GameWWW.model.dto.responce.UserResp;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private TeamService teamService;

    @Spy
    private ObjectMapper mapper;

    @Test
    public void getAllUsers() {
        User user1 = new User();
        user1.setId(1L);
        user1.setUserName("User1");

        User user2 = new User();
        user2.setId(2L);
        user2.setUserName("User2");
        List<User> userList = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        List<UserResp> resp = userService.getAllUsers();
        assertEquals(userList.get(0).getId(), resp.get(0).getId());
        assertEquals(userList.get(1).getId(), resp.get(1).getId());
    }

    @Test
    public void getUserFromBD() {
        User user = new User();
        user.setId(1L);
        user.setUserName("User1");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        User userDB = userService.getUserFromBD(user.getId());
        assertEquals(user.getUserName(), userDB.getUserName());
    }

    @Test
    public void getUser() {
        User user = new User();
        user.setId(1L);
        user.setUserName("User1");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        UserResp userResp = userService.getUser(user.getId());
        assertEquals(user.getUserName(), userResp.getUserName());
    }

    @Test
    public void updateUser() {
        UserReq req = new UserReq();
        req.setUserName("User1");
        req.setPassword("1234");

        User user = new User();
        user.setId(1L);
        user.setUserName("UserTest");
        user.setPassword("abcd");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        userService.updateUser(user.getId(), req);
        assertEquals(req.getPassword(), user.getPassword());
        assertEquals(req.getUserName(), user.getUserName());
    }

    @Test
    public void deleteUser() {
        User user = new User();
        user.setId(1L);
        Team team = new Team();
        team.setId(1L);
        user.setTeam(team);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        userService.deleteUser(user.getId());
        verify(userRepository, times(1)).deleteById(user.getId());
        verify(teamService,times(1)).deleteTeam(team.getId());
    }
}