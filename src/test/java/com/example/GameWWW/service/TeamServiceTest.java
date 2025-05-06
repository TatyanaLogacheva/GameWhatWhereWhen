package com.example.GameWWW.service;

import com.example.GameWWW.exceptions.CommonBackendException;
import com.example.GameWWW.model.db.entity.Player;
import com.example.GameWWW.model.db.entity.PlayerTeam;
import com.example.GameWWW.model.db.entity.Team;
import com.example.GameWWW.model.db.repository.PlayerRepository;
import com.example.GameWWW.model.db.repository.PlayerTeamRepository;
import com.example.GameWWW.model.db.repository.TeamRepository;
import com.example.GameWWW.model.dto.request.PlayerTeamReq;
import com.example.GameWWW.model.dto.request.TeamInfoReq;
import com.example.GameWWW.model.dto.responce.PlayerInfoResp;
import com.example.GameWWW.model.dto.responce.TeamInfoResp;
import com.example.GameWWW.model.enums.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TeamServiceTest {
    @InjectMocks
    private TeamService teamService;

    @Mock
    private TeamRepository teamRepository;
    @Mock
    private PlayerTeamRepository playerTeamRepository;
    @Mock
    private PlayerService playerService;
    @Mock
    private PlayerRepository playerRepository;

    @Spy
    private ObjectMapper mapper;

    @Test
    public void addTeam() {
        TeamInfoReq req = new TeamInfoReq("TestTeam", "RF", "SPB");

        Team team = new Team();
        team.setId(1L);
        team.setTeamName(req.getTeamName());
        team.setCountry(req.getCountry());
        team.setCity(req.getCity());
        when(teamRepository.save(any(Team.class))).thenReturn(team);
        TeamInfoResp resp = teamService.addTeam(req);
        assertEquals(team.getId(), resp.getId());
    }

    @Test(expected = CommonBackendException.class)
    public void addTeamAlreadyExisted() {
        TeamInfoReq req = new TeamInfoReq();
        req.setTeamName("TestTeam");

        Team team = new Team();
        team.setId(1L);
        team.setTeamName(req.getTeamName());
        when(teamRepository.findTeamByTeamName(req.getTeamName())).thenReturn(Optional.of(team));
        teamService.addTeam(req);
    }

    @Test
    public void getTeam() {
        Team team = new Team();
        team.setId(1L);
        team.setTeamName("TestTeam");

        when(teamRepository.findById(team.getId())).thenReturn(Optional.of(team));
        TeamInfoResp teamResp = teamService.getTeam(team.getId());
        assertEquals(team.getTeamName(), teamResp.getTeamName());
    }

    @Test
    public void getTeamFromDB() {
        Team team = new Team();
        team.setId(1L);
        team.setTeamName("TestTeam");

        when(teamRepository.findById(team.getId())).thenReturn(Optional.of(team));
        Team teamFromDB = teamService.getTeamFromDB(team.getId());
        assertEquals(team.getTeamName(), teamFromDB.getTeamName());
    }

    @Test(expected = CommonBackendException.class)
    public void getTeamFromDBNotFound() {
        teamService.getTeamFromDB(1L);
    }

    @Test
    public void updateTeam() {
        TeamInfoReq req = new TeamInfoReq();
        req.setTeamName("NameTeam");
        req.setCity("Msc");

        Team team = new Team();
        team.setId(1L);
        team.setTeamName("TestTeam");
        team.setCity("SPB");
        when(teamRepository.findById(team.getId())).thenReturn(Optional.of(team));
        teamService.updateTeam(team.getId(), req);
        assertEquals(req.getTeamName(), team.getTeamName());
        assertEquals(req.getCity(), team.getCity());
    }

    @Test
    public void deleteTeam() {
        Team team = new Team();
        team.setId(1L);
        PlayerTeam playerTeam = new PlayerTeam();
        playerTeam.setId(1L);
        team.setPlayerTeams(List.of(playerTeam));
        when(teamRepository.findById(team.getId())).thenReturn(Optional.of(team));
        teamService.deleteTeam(team.getId());
        verify(teamRepository, times(1)).save(team);
        verify(playerTeamRepository, times(1)).deleteById(playerTeam.getId());
        assertEquals(Status.DELETED, team.getStatus());
    }

    @Test
    public void getAllTeams() {
        Team team1 = new Team();
        team1.setId(1L);
        team1.setTeamName("TestTeam");

        Team team2 = new Team();
        team2.setId(2L);
        team2.setTeamName("NameTeam");
        List<Team> teamList = Arrays.asList(team1, team2);

        when(teamRepository.findAll()).thenReturn(List.of(team1, team2));
        List<TeamInfoResp> respList = teamService.getAllTeams();
        assertEquals(teamList.get(0).getId(), respList.get(0).getId());
        assertEquals(teamList.get(1).getId(), respList.get(1).getId());
    }

    @Test
    public void getAllExistsTeams() {
        Team team1 = new Team();
        team1.setId(1L);
        team1.setTeamName("TestTeam");
        team1.setStatus(Status.CREATED);

        Team team2 = new Team();
        team2.setId(2L);
        team2.setTeamName("NameTeam");
        team2.setStatus(Status.DELETED);
        List<Team> teamList = Arrays.asList(team1);
        when(teamRepository.findTeamsByStatusNot(Status.DELETED)).thenReturn(List.of(team1));
        List<TeamInfoResp> respList = teamService.getAllExistsTeams();
        assertEquals(teamList.size(), respList.size());
        assertEquals(teamList.get(0).getId(), respList.get(0).getId());
    }

    @Test
    public void updateAnswerList() {
        Team team = new Team();
        team.setId(1L);
        teamService.updateAnswerList(team);
        verify(teamRepository, times(1)).save(team);
    }

    @Test
    public void linkTeamAndPlayer() {
        Team team = new Team();
        team.setId(1L);
        when(teamRepository.findById(team.getId())).thenReturn(Optional.of(team));

        Player player = new Player();
        player.setId(1L);

        Player player2 = new Player();
        player2.setId(3L);
        when(playerService.getPlayerFromDB(player.getId())).thenReturn(player);
        List<PlayerTeam> playerTeamListToPlayer = new ArrayList<>();
        player.setPlayerTeams(playerTeamListToPlayer);
        PlayerTeamReq playerTeamReq = new PlayerTeamReq();
        playerTeamReq.setCapitan(false);

        PlayerTeam playerTeam = new PlayerTeam();
        playerTeam.setId(1L);
        playerTeam.setCapitan(playerTeamReq.isCapitan());


        PlayerTeam exPlayerTeam = new PlayerTeam();
        exPlayerTeam.setId(2L);
        exPlayerTeam.setPlayer(player2);

        List<PlayerTeam> playerTeamListToTeam = new ArrayList<>();
        playerTeamListToTeam.add(exPlayerTeam);
        team.setPlayerTeams(playerTeamListToTeam);

        when(playerTeamRepository.save(any(PlayerTeam.class))).thenReturn(playerTeam);
        when(playerRepository.save(any(Player.class))).thenReturn(player);
        TeamInfoResp resp = teamService.linkTeamAndPlayer(team.getId(), player.getId(), playerTeamReq);
        assertEquals(team.getId(), resp.getId());
        assertEquals(playerTeam.isCapitan(), team.getPlayerTeams().get(0).isCapitan());
        assertEquals(player.getId(), team.getPlayerTeams().get(1).getPlayer().getId());
    }

    @Test
    public void changeCapitan() {
        Team team = new Team();
        team.setId(1L);
        when(teamRepository.findById(team.getId())).thenReturn(Optional.of(team));
        PlayerTeam playerTeam = new PlayerTeam();
        playerTeam.setId(1L);
        playerTeam.setCapitan(false);
        team.setPlayerTeams(List.of(playerTeam));
        Player player = new Player();
        player.setId(1L);
        playerTeam.setPlayer(player);
        when(playerTeamRepository.findPlayerTeamByTeam_IdAndPlayer_Id(team.getId(),
                player.getId())).thenReturn(playerTeam);
        teamService.changeCapitan(team.getId(), player.getId());
        assertTrue(team.getPlayerTeams().get(0).isCapitan());
        assertEquals(player.getId(), team.getPlayerTeams().get(0).getPlayer().getId());
    }

    @Test
    public void getAllPlayers() {
        Team team = new Team();
        team.setId(1L);
        PlayerTeam playerTeam = new PlayerTeam();
        playerTeam.setId(1L);
        team.setPlayerTeams(List.of(playerTeam));
        Player player = new Player();
        player.setId(1L);
        playerTeam.setPlayer(player);
        when(teamRepository.findById(team.getId())).thenReturn(Optional.of(team));
        List<PlayerInfoResp> allPlayers = teamService.getAllPlayers(team.getId());
        assertEquals(player.getId(), allPlayers.get(0).getId());
    }

    @Test
    public void deletePlayer() {
        Team team = new Team();
        team.setId(1L);
        PlayerTeam playerTeam = new PlayerTeam();
        playerTeam.setId(1L);
        team.setPlayerTeams(List.of(playerTeam));
        Player player = new Player();
        player.setId(1L);

        playerTeam.setTeam(team);
        playerTeam.setPlayer(player);
        when(playerTeamRepository.findPlayerTeamByTeam_IdAndPlayer_Id(team.getId(), player.getId())).thenReturn(playerTeam);
        teamService.deletePlayer(team.getId(), player.getId());
        verify(playerTeamRepository, times(1)).deleteById(playerTeam.getId());
    }
}