package com.example.GameWWW.service;

import com.example.GameWWW.exceptions.CommonBackendException;
import com.example.GameWWW.model.db.entity.Player;
import com.example.GameWWW.model.db.entity.Team;
import com.example.GameWWW.model.db.repository.PlayerRepository;
import com.example.GameWWW.model.db.repository.PlayerTeamRepository;
import com.example.GameWWW.model.dto.request.PlayerInfoReq;
import com.example.GameWWW.model.dto.responce.PlayerInfoResp;
import com.example.GameWWW.model.dto.responce.TeamInfoResp;
import com.example.GameWWW.model.enums.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlayerService {
    private final ObjectMapper objectMapper;
    private final PlayerRepository playerRepository;
    private final PlayerTeamRepository playerTeamRepository;

    public PlayerInfoResp addPlayer(PlayerInfoReq req) {
        if (!EmailValidator.getInstance().isValid(req.getEmail())) {
            throw new CommonBackendException("Email is invalid", HttpStatus.BAD_REQUEST);
        }
        playerRepository.findByEmail(req.getEmail()).ifPresent(player -> {
            throw new CommonBackendException("Player is already existed", HttpStatus.CONFLICT);
        });

        Player player = objectMapper.convertValue(req, Player.class);
        player.setStatus(Status.CREATED);
        Player save = playerRepository.save(player);
        return objectMapper.convertValue(save, PlayerInfoResp.class);
    }

    public PlayerInfoResp getPlayer(Long id) {
        Player player = getPlayerFromDB(id);
        return objectMapper.convertValue(player, PlayerInfoResp.class);
    }

    public Player getPlayerFromDB(Long id) {
        Optional<Player> optionalPlayer = playerRepository.findById(id);
        final String errorMessage = String.format("Player with id %s is not found", id);
        return optionalPlayer.orElseThrow(() -> new CommonBackendException(errorMessage, HttpStatus.NOT_FOUND));
    }

    public PlayerInfoResp updatePlayer(Long id, PlayerInfoReq req) {
        if (!EmailValidator.getInstance().isValid(req.getEmail())) {
            throw new CommonBackendException("Email is invalid", HttpStatus.BAD_REQUEST);
        }
        Player playerFromDB = getPlayerFromDB(id);
        Player playerReq = objectMapper.convertValue(req, Player.class);

        playerFromDB.setFirstName(playerReq.getFirstName() == null ? playerReq.getFirstName() : playerReq.getFirstName());
        playerFromDB.setLastName(playerReq.getLastName() == null ? playerReq.getLastName() : playerReq.getLastName());
        playerFromDB.setMiddleName(playerReq.getMiddleName() == null ? playerReq.getMiddleName() : playerReq.getMiddleName());
        playerFromDB.setGender(playerReq.getGender() == null ? playerReq.getGender() : playerReq.getGender());
        playerFromDB.setDateOfBirthday(playerReq.getDateOfBirthday() == null ? playerReq.getDateOfBirthday() : playerReq.getDateOfBirthday());
        playerFromDB.setTelephonNum(playerReq.getTelephonNum() == null ? playerReq.getTelephonNum() : playerReq.getTelephonNum());
        playerFromDB.setEmail(playerReq.getEmail() == null ? playerReq.getEmail() : playerReq.getEmail());
        playerFromDB.setPlaceOfWorkOrStudy(playerReq.getPlaceOfWorkOrStudy() == null ? playerReq.getPlaceOfWorkOrStudy() : playerReq.getPlaceOfWorkOrStudy());
        playerFromDB.setAdditionalInformation(playerReq.getAdditionalInformation() == null ? playerReq.getAdditionalInformation() : playerReq.getAdditionalInformation());

        playerFromDB.setStatus(Status.UPDATED);
        playerFromDB = playerRepository.save(playerFromDB);
        return objectMapper.convertValue(playerFromDB, PlayerInfoResp.class);
    }

    public void deletePlayer(Long id) {
        Player player = getPlayerFromDB(id);
        player.setStatus(Status.DELETED);
        playerRepository.save(player);
        player.getPlayerTeams().forEach(pt -> {
            playerTeamRepository.deleteById(pt.getId());
        });
    }

    public List<PlayerInfoResp> getAllPlayers() {
        return playerRepository.findAll().stream()
                .map(p -> objectMapper.convertValue(p, PlayerInfoResp.class))
                .collect(Collectors.toList());
    }

    public List<PlayerInfoResp> getAllExistsPlayers() {
        return playerRepository.findPlayerByStatusNot(Status.DELETED).stream()
                .map(p -> objectMapper.convertValue(p, PlayerInfoResp.class))
                .collect(Collectors.toList());
    }

    public List<TeamInfoResp> getMyTeams(Long id) {
        Player player = getPlayerFromDB(id);
        List<Team> myTeams = player.getPlayerTeams().stream()
                .map(playerTeam -> playerTeam.getTeam()).collect(Collectors.toList());
        return myTeams.stream().map(pt -> objectMapper.convertValue(pt, TeamInfoResp.class))
                .collect(Collectors.toList());
    }
}
