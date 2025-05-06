package com.example.GameWWW.service;

import com.example.GameWWW.exceptions.CommonBackendException;
import com.example.GameWWW.model.db.entity.Role;
import com.example.GameWWW.model.db.entity.Team;
import com.example.GameWWW.model.db.entity.User;
import com.example.GameWWW.model.db.repository.UserRepository;
import com.example.GameWWW.model.dto.request.RoleReq;
import com.example.GameWWW.model.dto.request.TeamInfoReq;
import com.example.GameWWW.model.dto.request.UserReq;
import com.example.GameWWW.model.dto.responce.TeamInfoResp;
import com.example.GameWWW.model.dto.responce.UserResp;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    private final TeamService teamService;

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        Optional<User> opt = userRepository.findUserByUserName(name);

        if ((opt.isEmpty()))
            throw new UsernameNotFoundException("User with name " + name + " is not found!");
        else {
            User user = opt.get();
            return new org.springframework.security.core.userdetails.User(
                    user.getUserName(),
                    user.getPassword(),
                    user.getRoles()
                            .stream()
                            .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
                            .collect(Collectors.toSet())
            );
        }
    }

    public List<UserResp> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> objectMapper.convertValue(user, UserResp.class)).collect(Collectors.toList());
    }

    public User getUserFromBD(Long id) {
        Optional<User> user = userRepository.findById(id);
        final String errorMsg = String.format("User with id %d is not found", id);
        return user.orElseThrow(() -> new CommonBackendException(errorMsg, HttpStatus.NOT_FOUND));
    }

    public UserResp getUser(Long id) {
        User user = getUserFromBD(id);
        return objectMapper.convertValue(user, UserResp.class);
    }

    public UserResp updateUser(Long id, UserReq req) {
        User userFromDB = getUserFromBD(id);
        User userReq = objectMapper.convertValue(req, User.class);

        userFromDB.setUserName(userReq.getUserName() == null ? userFromDB.getUserName() : userReq.getUserName());
        userFromDB.setPassword(userReq.getPassword() == null ? userFromDB.getPassword() : userReq.getPassword());

        userFromDB = userRepository.save(userFromDB);
        return objectMapper.convertValue(userFromDB, UserResp.class);
    }

    public UserResp addUser(UserReq reqU, TeamInfoReq reqT) {
        User user = objectMapper.convertValue(reqU, User.class);
        if (!userRepository.findUserByUserName(user.getUserName()).isEmpty()) {
            throw new CommonBackendException("User with this name already exists!", HttpStatus.CONFLICT);
        }
        Role role = objectMapper.convertValue(new RoleReq("ROLE_USER"), Role.class);
        user.setRoles(Collections.singleton(role));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        TeamInfoResp teamInfoResp = teamService.addTeam(reqT);
        Team team = objectMapper.convertValue(teamInfoResp, Team.class);
        user.setTeam(team);
        userRepository.save(user);
        UserResp userResp = objectMapper.convertValue(user, UserResp.class);
        userResp.setTeamInfoResp(teamInfoResp);
        return userResp;
    }

    public void deleteUser(Long id) {
        User user = getUserFromBD(id);
        teamService.deleteTeam(user.getTeam().getId());
        userRepository.deleteById(user.getId());
    }


}
