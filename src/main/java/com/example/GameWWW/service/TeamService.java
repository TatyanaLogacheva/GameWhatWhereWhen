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
import com.example.GameWWW.model.dto.responce.PlayerTeamResp;
import com.example.GameWWW.model.dto.responce.TeamInfoResp;
import com.example.GameWWW.model.enums.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamService {
    private final TeamRepository teamRepository;
    private final ObjectMapper objectMapper;
    private final PlayerService playerService;
    private final PlayerTeamRepository playerTeamRepository;
    private final PlayerRepository playerRepository;

    public TeamInfoResp addTeam(TeamInfoReq req) {
        teamRepository.findTeamByTeamName(req.getTeamName()).ifPresent(team -> {
            throw new CommonBackendException("Team with this name already exists", HttpStatus.CONFLICT);
        });
        Team team = objectMapper.convertValue(req, Team.class);
        team.setStatus(Status.CREATED);
        Team save = teamRepository.save(team);
        return objectMapper.convertValue(save, TeamInfoResp.class);
    }

    public TeamInfoResp getTeam(Long id) {
        Team teamFromDB = getTeamFromDB(id);
        return objectMapper.convertValue(teamFromDB, TeamInfoResp.class);
    }

    public Team getTeamFromDB(Long id) {
        Optional<Team> optionalTeam = teamRepository.findById(id);
        final String errorMsg = String.format("Team with id %s is not found", id);
        return optionalTeam.orElseThrow(() -> new CommonBackendException(errorMsg, HttpStatus.NOT_FOUND));
    }

    public TeamInfoResp updateTeam(Long id, TeamInfoReq req) {
        Team teamFromDB = getTeamFromDB(id);
        Team teamReq = objectMapper.convertValue(req, Team.class);

        teamFromDB.setTeamName(teamReq.getTeamName() == null ? teamFromDB.getTeamName() : teamReq.getTeamName());
        teamFromDB.setCountry(teamReq.getCountry() == null ? teamFromDB.getCountry() : teamReq.getCountry());
        teamFromDB.setCity(teamReq.getCity() == null ? teamFromDB.getCity() : teamReq.getCity());

        teamFromDB.setStatus(Status.UPDATED);
        teamFromDB = teamRepository.save(teamFromDB);
        return objectMapper.convertValue(teamFromDB, TeamInfoResp.class);
    }


    public void deleteTeam(Long id) {
        Team team = getTeamFromDB(id);
        team.setStatus(Status.DELETED);
        teamRepository.save(team);
        team.getPlayerTeams().forEach(playerTeam -> {
            playerTeamRepository.deleteById(playerTeam.getId());
        });
    }

    public List<TeamInfoResp> getAllTeams() {
        return teamRepository.findAll().stream()
                .map(t -> objectMapper.convertValue(t, TeamInfoResp.class))
                .collect(Collectors.toList());
    }

    public List<TeamInfoResp> getAllExistsTeams() {
        return teamRepository.findTeamsByStatusNot(Status.DELETED).stream()
                .map(t -> objectMapper.convertValue(t, TeamInfoResp.class))
                .collect(Collectors.toList());
    }

    public Team updateAnswerList(Team updateTeam) {
        return teamRepository.save(updateTeam);
    }

    public TeamInfoResp linkTeamAndPlayer(Long teamID, Long playerID, PlayerTeamReq req) {
        Team teamFromDB = getTeamFromDB(teamID);
        Player playerFromDB = playerService.getPlayerFromDB(playerID);
        PlayerTeam playerTeam = objectMapper.convertValue(req, PlayerTeam.class);
        if (teamFromDB.getId() == null || playerFromDB.getId() == null) {
            throw new CommonBackendException("Team or Player is not found", HttpStatus.NOT_FOUND);
        }

        List<PlayerTeam> playerTeamsToTeam = teamFromDB.getPlayerTeams();
        PlayerTeam existedPlayerTeam = playerTeamsToTeam.stream()
                .filter(pt -> pt.getPlayer().getId().equals(playerID)).findFirst().orElse(null);
        if (existedPlayerTeam != null) {
            throw new CommonBackendException("Player is already added", HttpStatus.CONFLICT);
        }
        if (playerTeamsToTeam.size() >= 6) {
            throw new CommonBackendException("The team has already 6 players." +
                    " You can't add more than 6 players", HttpStatus.CONFLICT);
        }
        playerTeamsToTeam.add(playerTeam);
        playerTeam.setTeam(teamFromDB);

        List<PlayerTeam> playerTeamsToPlayer = playerFromDB.getPlayerTeams();
        playerTeamsToPlayer.add(playerTeam);
        playerTeam.setPlayer(playerFromDB);

        teamRepository.save(teamFromDB);
        playerTeamRepository.save(playerTeam);

        if (playerTeam.getIsCapitan()) {
            changeCapitan(teamID, playerID);
        }
        playerRepository.save(playerFromDB);

        TeamInfoResp teamInfoResp = objectMapper.convertValue(teamFromDB, TeamInfoResp.class);
        PlayerTeamResp playerTeamResp = objectMapper.convertValue(playerTeam, PlayerTeamResp.class);
        PlayerInfoResp playerInfoResp = objectMapper.convertValue(playerFromDB, PlayerInfoResp.class);

        teamInfoResp.setPlayerTeam(playerTeamResp);
        teamInfoResp.setPlayer(playerInfoResp);

        return teamInfoResp;
    }

    public void changeCapitan(Long teamID, Long playerID) {
        Team team = getTeamFromDB(teamID);
        team.getPlayerTeams().forEach(playerTeam -> {
            if (playerTeam.getIsCapitan()) {
                playerTeam.setIsCapitan(false);
            }
        });
        PlayerTeam playerTeam = playerTeamRepository.findPlayerTeamByTeam_IdAndPlayer_Id(teamID, playerID);
        playerTeam.setIsCapitan(true);
        playerTeamRepository.save(playerTeam);
    }


    public List<PlayerInfoResp> getAllPlayers(Long id) {
        Team team = getTeamFromDB(id);
        List<PlayerInfoResp> allPlayers = team.getPlayerTeams().stream()
                .map(pt -> {
                    PlayerInfoResp pir = objectMapper.convertValue(pt.getPlayer(), PlayerInfoResp.class);
                    PlayerTeamResp ptr = objectMapper.convertValue(pt, PlayerTeamResp.class);
                    pir.setCapitan(ptr);
                    return pir;
                }).collect(Collectors.toList());
        return allPlayers;
    }

    public void deletePlayer(Long teamID, Long playerID) {
        PlayerTeam playerTeam = playerTeamRepository.findPlayerTeamByTeam_IdAndPlayer_Id(teamID, playerID);
        playerTeamRepository.deleteById(playerTeam.getId());
    }

}
