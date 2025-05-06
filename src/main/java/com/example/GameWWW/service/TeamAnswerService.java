package com.example.GameWWW.service;

import com.example.GameWWW.exceptions.CommonBackendException;
import com.example.GameWWW.model.db.entity.QuestionInfo;
import com.example.GameWWW.model.db.entity.Team;
import com.example.GameWWW.model.db.entity.TeamAnswer;
import com.example.GameWWW.model.db.repository.TeamAnswerRepository;
import com.example.GameWWW.model.dto.request.TeamAnswerInfoReq;
import com.example.GameWWW.model.dto.responce.TeamAnswerInfoResp;
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
public class TeamAnswerService {
    private final ObjectMapper objectMapper;
    private final TeamAnswerRepository teamAnswerRepository;
    private final TeamService teamService;
    private final QuestionInfoService questionInfoService;

    public TeamAnswerInfoResp addTeamAnswer(TeamAnswerInfoReq req, Long teamID, Long questionID) {
        teamAnswerRepository.findByQuestion_IdAndTeam_Id(questionID, teamID).ifPresent(teamAnswer -> {
            throw new CommonBackendException("Answer is already sent", HttpStatus.CONFLICT);
        });
        Team teamFromDB = teamService.getTeamFromDB(teamID);
        QuestionInfo questionInfo = questionInfoService.getQuestionFromDB(questionID);

        TeamAnswer teamAnswer = objectMapper.convertValue(req, TeamAnswer.class);
        teamAnswer.setStatus(Status.CREATED);
        TeamAnswer saveAnswer = teamAnswerRepository.save(teamAnswer);

        if (saveAnswer.getTextAnswer().equalsIgnoreCase(questionInfo.getAnswer())) {
            saveAnswer.setPoint(1);
        } else {
            saveAnswer.setPoint(0);
        }

        List<TeamAnswer> teamAnswerList = teamFromDB.getTeamAnswers();
        teamAnswerList.add(saveAnswer);
        Team team = teamService.updateAnswerList(teamFromDB);

        saveAnswer.setTeam(team);
        saveAnswer.setQuestion(questionInfo);
        teamAnswerRepository.save(saveAnswer);

        TeamAnswerInfoResp teamAnswerInfoResp = objectMapper.convertValue(saveAnswer, TeamAnswerInfoResp.class);
        TeamInfoResp teamInfoResp = objectMapper.convertValue(team, TeamInfoResp.class);

        teamAnswerInfoResp.setTeam(teamInfoResp);
        return teamAnswerInfoResp;
    }

    public TeamAnswer getTeamAnswerFromDB(Long id) {
        Optional<TeamAnswer> optionalTeamAnswer = teamAnswerRepository.findById(id);
        final String errorMessage = String.format("Answer with id %s is not found", id);
        return optionalTeamAnswer.orElseThrow(() -> new CommonBackendException(errorMessage, HttpStatus.NOT_FOUND));
    }

    public TeamAnswerInfoResp getTeamAnswer(Long id) {
        TeamAnswer teamAnswer = getTeamAnswerFromDB(id);
        return objectMapper.convertValue(teamAnswer, TeamAnswerInfoResp.class);
    }


    public TeamAnswerInfoResp updateTeamAnswer(Long id, TeamAnswerInfoReq req) {
        TeamAnswer teamAnswerFromDB = getTeamAnswerFromDB(id);
        TeamAnswer teamAnswerReq = objectMapper.convertValue(req, TeamAnswer.class);

        teamAnswerFromDB.setTextAnswer(teamAnswerReq.getTextAnswer() == null ? teamAnswerFromDB.getTextAnswer() :
                teamAnswerReq.getTextAnswer());
        teamAnswerFromDB.setPoint(teamAnswerReq.getPoint() == null ? teamAnswerFromDB.getPoint() :
                teamAnswerReq.getPoint());
        teamAnswerFromDB.setAppeal(teamAnswerReq.getAppeal() == null ? teamAnswerFromDB.getAppeal() :
                teamAnswerReq.getAppeal());

        teamAnswerFromDB.setStatus(Status.UPDATED);
        teamAnswerFromDB = teamAnswerRepository.save(teamAnswerFromDB);
        return objectMapper.convertValue(teamAnswerFromDB, TeamAnswerInfoResp.class);
    }

    public void deleteTeamAnswer(Long id) {
        TeamAnswer teamAnswer = getTeamAnswerFromDB(id);
        teamAnswer.setStatus(Status.DELETED);
        teamAnswerRepository.save(teamAnswer);
    }

    public List<TeamAnswerInfoResp> getAllTeamAnswers() {
        return teamAnswerRepository.findAll().stream()
                .map(teamAnswer -> objectMapper.convertValue(teamAnswer, TeamAnswerInfoResp.class))
                .collect(Collectors.toList());
    }
}
