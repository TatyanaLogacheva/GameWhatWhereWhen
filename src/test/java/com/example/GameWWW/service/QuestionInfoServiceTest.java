package com.example.GameWWW.service;

import com.example.GameWWW.exceptions.CommonBackendException;
import com.example.GameWWW.model.db.entity.AuthorOfQuestion;
import com.example.GameWWW.model.db.entity.Game;
import com.example.GameWWW.model.db.entity.QuestionInfo;
import com.example.GameWWW.model.db.entity.Tour;
import com.example.GameWWW.model.db.repository.QuestionInfoRepository;
import com.example.GameWWW.model.dto.request.QuestionInfoReq;
import com.example.GameWWW.model.dto.responce.QuestionInfoResp;
import com.example.GameWWW.model.enums.QuestionStatus;
import com.example.GameWWW.model.enums.QuestionType;
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
public class QuestionInfoServiceTest {
    @InjectMocks
    private QuestionInfoService questionService;

    @Mock
    private AuthorOfQuestionService authorService;
    @Mock
    private QuestionInfoRepository questionRepository;
    @Mock
    private TourService tourService;

    @Spy
    private ObjectMapper objectMapper;

    @Test
    public void addQuestion() {
        AuthorOfQuestion author = new AuthorOfQuestion();
        author.setId(1L);
        List<QuestionInfo> questionList = new ArrayList<>();
        author.setQuestions(questionList);
        when(authorService.getAuthorFromDB(author.getId())).thenReturn(author);
        when(authorService.updateQuestionsList(author)).thenReturn(author);

        QuestionInfoReq req = new QuestionInfoReq();
        req.setText("text");
        req.setAnswer("answer");
        req.setInfoSource("net");
        req.setType(QuestionType.ORDINARY);

        QuestionInfo question = new QuestionInfo();
        question.setId(1L);
        question.setText(req.getText());
        question.setAnswer(req.getAnswer());
        question.setInfoSource(req.getInfoSource());
        question.setType(req.getType());

        when(questionRepository.save(any(QuestionInfo.class))).thenReturn(question);

        QuestionInfoResp resp = questionService.addQuestion(req, author.getId());
        assertEquals(question.getId(), resp.getId());
        assertEquals(author.getId(), resp.getAuthor().getId());
    }

    @Test(expected = CommonBackendException.class)
    public void addQuestionExisted() {
        AuthorOfQuestion author = new AuthorOfQuestion();
        author.setId(1L);
        QuestionInfoReq req = new QuestionInfoReq();
        req.setText("text");

        QuestionInfo question = new QuestionInfo();
        question.setId(1L);
        question.setText(req.getText());

        when(questionRepository.findQuestionInfoByText(req.getText())).thenReturn(Optional.of(question));
        questionService.addQuestion(req, author.getId());
    }

    @Test
    public void getQuestionFromDB() {
        QuestionInfo question = new QuestionInfo();
        question.setId(1L);
        question.setText("text");
        question.setAnswer("answer");
        question.setInfoSource("net");
        question.setType(QuestionType.ORDINARY);
        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));

        QuestionInfo questionInfo = questionService.getQuestionFromDB(question.getId());
        assertEquals(question.getText(), questionInfo.getText());
    }

    @Test(expected = CommonBackendException.class)
    public void getQuestionFromDBNotFound() {
        questionService.getQuestionFromDB(1L);
    }

    @Test
    public void getQuestion() {
        AuthorOfQuestion author = new AuthorOfQuestion();
        author.setId(1L);

        QuestionInfo question = new QuestionInfo();
        question.setId(1L);
        question.setText("text");
        question.setAnswer("answer");
        question.setInfoSource("net");
        question.setType(QuestionType.ORDINARY);
        question.setAuthor(author);

        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));
        QuestionInfoResp resp = questionService.getQuestion(question.getId());

        assertEquals(question.getText(), resp.getText());
        assertEquals(question.getAuthor().getId(), resp.getAuthor().getId());
    }

    @Test
    public void updateQuestion() {
        QuestionInfo question = new QuestionInfo();
        question.setId(1L);
        question.setText("text");
        question.setAnswer("answer");
        question.setInfoSource("net");
        question.setType(QuestionType.ORDINARY);

        QuestionInfoReq req = new QuestionInfoReq();
        req.setText("body");
        req.setInfoSource("book");

        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));
        questionService.updateQuestion(question.getId(), req);
        assertEquals(question.getText(), req.getText());
        assertEquals(question.getInfoSource(), req.getInfoSource());
    }

    @Test
    public void deleteQuestion() {
        QuestionInfo question = new QuestionInfo();
        question.setId(1L);
        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));
        questionService.deleteQuestion(question.getId());
        verify(questionRepository, times(1)).save(any(QuestionInfo.class));
        assertEquals(QuestionStatus.DELETED, question.getStatus());
    }

    @Test
    public void getAllQuestions() {
        QuestionInfo question1 = new QuestionInfo();
        question1.setId(1L);
        question1.setText("text");
        question1.setAnswer("answer");
        question1.setInfoSource("net");
        question1.setType(QuestionType.ORDINARY);

        QuestionInfo question2 = new QuestionInfo();
        question2.setId(2L);
        question2.setText("body");
        question2.setAnswer("responce");
        question2.setInfoSource("book");
        question2.setType(QuestionType.ADDITIONAL_MATERIAL);

        List<QuestionInfo> questions = Arrays.asList(question1, question2);
        when(questionRepository.findAll()).thenReturn(List.of(question1, question2));
        List<QuestionInfoResp> questionResp = questionService.getAllQuestions();
        assertEquals(questions.get(0).getId(), questionResp.get(0).getId());
        assertEquals(questions.get(1).getId(), questionResp.get(1).getId());
    }

    @Test
    public void linkTourAndQuestion() {
        Tour tour = new Tour();
        tour.setId(1L);
        when(tourService.getTourFromDB(tour.getId())).thenReturn(tour);

        List<QuestionInfo> questionList = new ArrayList<>();
        tour.setQuestions(questionList);

        Game game = new Game();
        game.setId(1L);
        tour.setGame(game);

        QuestionInfo question = new QuestionInfo();
        question.setId(1L);
        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));

        when(tourService.updateTourQuestionsList(tour)).thenReturn(tour);
        QuestionInfoResp resp = questionService.linkTourAndQuestion(tour.getId(), question.getId());
        assertEquals(resp.getId(), question.getId());
        assertEquals(resp.getTour().getId(), tour.getId());
        assertEquals(resp.getTour().getGame().getId(), game.getId());
    }
}