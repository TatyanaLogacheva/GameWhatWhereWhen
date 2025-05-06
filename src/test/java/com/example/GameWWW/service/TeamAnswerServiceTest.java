package com.example.GameWWW.service;

import com.example.GameWWW.exceptions.CommonBackendException;
import com.example.GameWWW.model.db.entity.QuestionInfo;
import com.example.GameWWW.model.db.entity.Team;
import com.example.GameWWW.model.db.entity.TeamAnswer;
import com.example.GameWWW.model.db.repository.TeamAnswerRepository;
import com.example.GameWWW.model.dto.request.TeamAnswerInfoReq;
import com.example.GameWWW.model.dto.responce.TeamAnswerInfoResp;
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
public class TeamAnswerServiceTest {
    @InjectMocks
    private TeamAnswerService teamAnswerService;

    @Mock
    private TeamAnswerRepository teamAnswerRepository;
    @Mock
    private TeamService teamService;
    @Mock
    private QuestionInfoService questionInfoService;

    @Spy
    private ObjectMapper mapper;

    @Test
    public void addTeamAnswer() {
        TeamAnswerInfoReq teamAnswerReq = new TeamAnswerInfoReq();
        teamAnswerReq.setTextAnswer("answer");

        TeamAnswer teamAnswer = new TeamAnswer();
        teamAnswer.setId(1L);
        teamAnswer.setTextAnswer(teamAnswerReq.getTextAnswer());

        Team team = new Team();
        team.setId(1L);
        List<TeamAnswer> teamAnswerList = new ArrayList<>();
        team.setTeamAnswers(teamAnswerList);
        when(teamService.getTeamFromDB(team.getId())).thenReturn(team);

        QuestionInfo question = new QuestionInfo();
        question.setId(1L);
        question.setAnswer("Answer");
        when(questionInfoService.getQuestionFromDB(question.getId())).thenReturn(question);

        when(teamAnswerRepository.save(any(TeamAnswer.class))).thenReturn(teamAnswer);
        when(teamService.updateAnswerList(team)).thenReturn(team);
        TeamAnswerInfoResp resp = teamAnswerService.addTeamAnswer(teamAnswerReq, team.getId(), question.getId());
        assertEquals(teamAnswer.getId(), resp.getId());
        assertEquals(team.getId(), resp.getTeam().getId());
        assertEquals(question.getId(), resp.getQuestion().getId());
        assertTrue(resp.getPoint() > 0);
    }

    @Test(expected = CommonBackendException.class)
    public void addAnswerAlreadySent() {
        TeamAnswerInfoReq teamAnswerReq = new TeamAnswerInfoReq();
        teamAnswerReq.setTextAnswer("answer");

        Team team = new Team();
        team.setId(1L);

        QuestionInfo question = new QuestionInfo();
        question.setId(1L);

        TeamAnswer teamAnswer = new TeamAnswer();
        teamAnswer.setId(1L);
        teamAnswer.setTeam(team);
        teamAnswer.setQuestion(question);

        when(teamAnswerRepository.findByQuestion_IdAndTeam_Id(teamAnswer.getQuestion().getId(),
                teamAnswer.getTeam().getId())).thenReturn(Optional.of(teamAnswer));
        teamAnswerService.addTeamAnswer(teamAnswerReq, team.getId(), question.getId());
    }

    @Test
    public void getTeamAnswerFromDB() {
        TeamAnswer teamAnswer = new TeamAnswer();
        teamAnswer.setId(1L);
        teamAnswer.setTextAnswer("answer");

        when(teamAnswerRepository.findById(teamAnswer.getId())).thenReturn(Optional.of(teamAnswer));
        TeamAnswer taFromDB = teamAnswerService.getTeamAnswerFromDB(teamAnswer.getId());
        assertEquals(teamAnswer.getTextAnswer(), taFromDB.getTextAnswer());
    }

    @Test(expected = CommonBackendException.class)
    public void getTeamAnswerFromDBNotFound() {
        teamAnswerService.getTeamAnswerFromDB(1L);
    }

    @Test
    public void getTeamAnswer() {
        TeamAnswer teamAnswer = new TeamAnswer();
        teamAnswer.setId(1L);
        teamAnswer.setTextAnswer("answer");

        when(teamAnswerRepository.findById(teamAnswer.getId())).thenReturn(Optional.of(teamAnswer));
        TeamAnswerInfoResp resp = teamAnswerService.getTeamAnswer(teamAnswer.getId());
        assertEquals(teamAnswer.getTextAnswer(), resp.getTextAnswer());
    }

    @Test
    public void updateTeamAnswer() {
        TeamAnswerInfoReq req = new TeamAnswerInfoReq();
        req.setTextAnswer("body");
        req.setPoint(1);

        TeamAnswer teamAnswer = new TeamAnswer();
        teamAnswer.setId(1L);
        teamAnswer.setTextAnswer("answer");
        teamAnswer.setPoint(0);
        when(teamAnswerRepository.findById(teamAnswer.getId())).thenReturn(Optional.of(teamAnswer));
        teamAnswerService.updateTeamAnswer(teamAnswer.getId(), req);
        assertEquals(req.getTextAnswer(), teamAnswer.getTextAnswer());
        assertEquals(req.getPoint(), teamAnswer.getPoint());
    }

    @Test
    public void deleteTeamAnswer() {
        TeamAnswer teamAnswer = new TeamAnswer();
        teamAnswer.setId(1L);
        when(teamAnswerRepository.findById(teamAnswer.getId())).thenReturn(Optional.of(teamAnswer));
        teamAnswerService.deleteTeamAnswer(teamAnswer.getId());
        verify(teamAnswerRepository, times(1)).save(any(TeamAnswer.class));
        assertEquals(Status.DELETED, teamAnswer.getStatus());
    }

    @Test
    public void getAllTeamAnswers() {
        TeamAnswer teamAnswer1 = new TeamAnswer();
        teamAnswer1.setId(1L);
        teamAnswer1.setTextAnswer("answer");

        TeamAnswer teamAnswer2 = new TeamAnswer();
        teamAnswer2.setId(2L);
        teamAnswer2.setTextAnswer("body");
        List<TeamAnswer> teamAnswerList = Arrays.asList(teamAnswer1, teamAnswer2);

        when(teamAnswerRepository.findAll()).thenReturn(List.of(teamAnswer1, teamAnswer2));
        List<TeamAnswerInfoResp> respList = teamAnswerService.getAllTeamAnswers();
        assertEquals(teamAnswerList.get(0).getId(), respList.get(0).getId());
        assertEquals(teamAnswerList.get(1).getId(), respList.get(1).getId());
    }
}