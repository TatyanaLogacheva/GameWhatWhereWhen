package com.example.GameWWW.service;

import com.example.GameWWW.exceptions.CommonBackendException;
import com.example.GameWWW.model.db.entity.Player;
import com.example.GameWWW.model.db.entity.PlayerTeam;
import com.example.GameWWW.model.db.entity.Team;
import com.example.GameWWW.model.db.repository.PlayerRepository;
import com.example.GameWWW.model.db.repository.PlayerTeamRepository;
import com.example.GameWWW.model.dto.request.PlayerInfoReq;
import com.example.GameWWW.model.dto.responce.PlayerInfoResp;
import com.example.GameWWW.model.dto.responce.TeamInfoResp;
import com.example.GameWWW.model.enums.Gender;
import com.example.GameWWW.model.enums.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PlayerServiceTest {
    @InjectMocks
    private PlayerService playerService;

    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private PlayerTeamRepository playerTeamRepository;

    @Spy
    private ObjectMapper mapper;

    @Test
    public void addPlayer() {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        PlayerInfoReq req = new PlayerInfoReq();
        req.setFirstName("Adam");
        req.setLastName("Adamov");
        req.setGender(Gender.MALE);
        req.setDateOfBirthday(new Date(645028861000L));
        req.setTelephonNum("+79567842325");
        req.setEmail("adam@mail.ru");
        req.setPlaceOfWorkOrStudy("BBC");

        Player player = new Player();
        player.setId(1L);
        player.setFirstName(req.getFirstName());
        player.setLastName(req.getLastName());
        player.setGender(req.getGender());
        player.setDateOfBirthday(format.format(req.getDateOfBirthday()));
        player.setTelephonNum(req.getTelephonNum());
        player.setEmail(req.getEmail());
        player.setPlaceOfWorkOrStudy(req.getPlaceOfWorkOrStudy());

        when(playerRepository.save(any((Player.class)))).thenReturn(player);

        PlayerInfoResp resp = playerService.addPlayer(req);
        assertEquals(resp.getId(), player.getId());
    }

    @Test(expected = CommonBackendException.class)
    public void getPlayerEmailInvalid() {
        PlayerInfoReq req = new PlayerInfoReq();
        req.setEmail("adammail.ru");

        playerService.addPlayer(req);
    }

    @Test(expected = CommonBackendException.class)
    public void addPlayerExisted() {
        PlayerInfoReq req = new PlayerInfoReq();
        req.setEmail("adam@mail.ru");

        Player player = new Player();
        player.setId(1L);
        player.setEmail(req.getEmail());

        when(playerRepository.findByEmail(player.getEmail())).thenReturn(Optional.of(player));
        playerService.addPlayer(req);
    }

    @Test
    public void getPlayer() {
        Player player = new Player();
        player.setId(1L);
        player.setEmail("adam@mail.ru");

        when(playerRepository.findById(player.getId())).thenReturn(Optional.of(player));
        PlayerInfoResp playerResp = playerService.getPlayer(player.getId());
        assertEquals(playerResp.getEmail(), player.getEmail());

    }

    @Test
    public void getPlayerFromDB() {
        Player player = new Player();
        player.setId(1L);
        player.setEmail("adam@mail.ru");

        when(playerRepository.findById(player.getId())).thenReturn(Optional.of(player));
        Player playerFromDB = playerService.getPlayerFromDB(player.getId());
        assertEquals(playerFromDB.getEmail(), player.getEmail());
    }

    @Test(expected = CommonBackendException.class)
    public void getPlayerFromDBNotFound() {
        playerService.getPlayerFromDB(1L);
    }

    @Test
    public void updatePlayer() {
        PlayerInfoReq req = new PlayerInfoReq();
        req.setTelephonNum("+79814569785");
        req.setEmail("bbc@mail.ru");

        Player player = new Player();
        player.setId(1L);
        player.setFirstName("Adam");
        player.setLastName("Adamov");
        player.setGender(Gender.MALE);
        player.setTelephonNum("+79567842325");
        player.setEmail("adam@mail.ru");
        player.setPlaceOfWorkOrStudy("BBC");
        when(playerRepository.findById(player.getId())).thenReturn(Optional.of(player));
        playerService.updatePlayer(player.getId(), req);
        assertEquals(player.getTelephonNum(), req.getTelephonNum());
        assertEquals(player.getEmail(), req.getEmail());
    }

    @Test
    public void deletePlayer() {
        Player player = new Player();
        player.setId(1L);
        PlayerTeam playerTeam = new PlayerTeam();
        playerTeam.setId(1L);
        player.setPlayerTeams(List.of(playerTeam));
        when(playerRepository.findById(player.getId())).thenReturn(Optional.of(player));
        playerService.deletePlayer(player.getId());
        verify(playerRepository, times(1)).save(any(Player.class));
        verify(playerTeamRepository, times(1)).deleteById(playerTeam.getId());
        assertEquals(Status.DELETED, player.getStatus());
    }

    @Test
    public void getAllPlayers() {
        Player player1 = new Player();
        player1.setId(1L);
        player1.setFirstName("Adam");
        player1.setTelephonNum("+79567842325");
        player1.setEmail("adam@mail.ru");
        Player player2 = new Player();
        player2.setId(2L);
        player2.setFirstName("Eva");
        player2.setTelephonNum("+79812569487");
        player2.setEmail("eva@mail.ru");
        List<Player> playerList = Arrays.asList(player1, player2);
        when(playerRepository.findAll()).thenReturn(List.of(player1, player2));
        List<PlayerInfoResp> respList = playerService.getAllPlayers();
        assertEquals(respList.get(0).getId(), playerList.get(0).getId());
        assertEquals(respList.get(1).getId(), playerList.get(1).getId());
    }

    @Test
    public void getAllExistsPlayers() {
        Player player1 = new Player();
        player1.setId(1L);
        player1.setFirstName("Adam");
        player1.setStatus(Status.DELETED);
        Player player2 = new Player();
        player2.setId(2L);
        player2.setFirstName("Eva");
        player2.setStatus(Status.CREATED);
        List<Player> playerList = Arrays.asList(player2);
        when(playerRepository.findPlayerByStatusNot(Status.DELETED)).thenReturn(List.of(player2));
        List<PlayerInfoResp> respList = playerService.getAllExistsPlayers();
        assertEquals(playerList.size(), respList.size());
        assertEquals(respList.get(0).getId(), playerList.get(0).getId());
    }

    @Test
    public void getMyTeams() {
        Player player = new Player();
        player.setId(1L);

        PlayerTeam playerTeam = new PlayerTeam();
        playerTeam.setId(1L);

        Team team = new Team();
        team.setId(1L);
        List<Team> teams = List.of(team);

        playerTeam.setTeam(team);
        player.setPlayerTeams(List.of(playerTeam));
        when(playerRepository.findById(player.getId())).thenReturn(Optional.of(player));
        List<TeamInfoResp> respTeams = playerService.getMyTeams(player.getId());
        assertEquals(teams.get(0).getId(), respTeams.get(0).getId());

    }
}