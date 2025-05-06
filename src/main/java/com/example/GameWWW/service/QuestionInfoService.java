package com.example.GameWWW.service;

import com.example.GameWWW.exceptions.CommonBackendException;
import com.example.GameWWW.model.db.entity.AuthorOfQuestion;
import com.example.GameWWW.model.db.entity.Game;
import com.example.GameWWW.model.db.entity.QuestionInfo;
import com.example.GameWWW.model.db.entity.Tour;
import com.example.GameWWW.model.db.repository.QuestionInfoRepository;
import com.example.GameWWW.model.dto.request.QuestionInfoReq;
import com.example.GameWWW.model.dto.responce.AuthorOfQuestionResp;
import com.example.GameWWW.model.dto.responce.GameInfoResp;
import com.example.GameWWW.model.dto.responce.QuestionInfoResp;
import com.example.GameWWW.model.dto.responce.TourInfoResp;
import com.example.GameWWW.model.enums.QuestionStatus;
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
public class QuestionInfoService {
    private final ObjectMapper objectMapper;
    private final QuestionInfoRepository questionRepository;
    private final AuthorOfQuestionService authorOfQuestionService;
    private final TourService tourService;


    public QuestionInfoResp addQuestion(QuestionInfoReq req, Long id) {
        questionRepository.findQuestionInfoByText(req.getText()).ifPresent(questionInfo -> {
            throw new CommonBackendException("Question already exists", HttpStatus.CONFLICT);
        });
        AuthorOfQuestion authorFromDB = authorOfQuestionService.getAuthorFromDB(id);
        QuestionInfo questionInfo = objectMapper.convertValue(req, QuestionInfo.class);

        questionInfo.setStatus(QuestionStatus.IN_BASE);
        QuestionInfo saveQuestion = questionRepository.save(questionInfo);

        List<QuestionInfo> questionInfoList = authorFromDB.getQuestions();
        questionInfoList.add(saveQuestion);
        AuthorOfQuestion author = authorOfQuestionService.updateQuestionsList(authorFromDB);

        saveQuestion.setAuthor(author);
        questionRepository.save(saveQuestion);

        QuestionInfoResp questionInfoResp = objectMapper.convertValue(saveQuestion, QuestionInfoResp.class);
        AuthorOfQuestionResp authorOfQuestionResp = objectMapper.convertValue(author, AuthorOfQuestionResp.class);

        questionInfoResp.setAuthor(authorOfQuestionResp);
        return questionInfoResp;
    }

    public QuestionInfo getQuestionFromDB(Long id) {
        Optional<QuestionInfo> optionalQuestionInfo = questionRepository.findById(id);
        final String errorMessage = String.format("Question with id %s is not found", id);
        return optionalQuestionInfo.orElseThrow(() -> new CommonBackendException(errorMessage, HttpStatus.NOT_FOUND));
    }

    public QuestionInfoResp getQuestion(Long id) {
        QuestionInfo questionInfo = getQuestionFromDB(id);
        AuthorOfQuestionResp author = objectMapper.convertValue(questionInfo.getAuthor(), AuthorOfQuestionResp.class);
        QuestionInfoResp questionInfoResp = objectMapper.convertValue(questionInfo, QuestionInfoResp.class);
        questionInfoResp.setAuthor(author);
        return questionInfoResp;
    }


    public QuestionInfoResp updateQuestion(Long id, QuestionInfoReq req) {
        QuestionInfo questionFromDB = getQuestionFromDB(id);
        QuestionInfo questionReq = objectMapper.convertValue(req, QuestionInfo.class);

        questionFromDB.setText(questionReq.getText() == null ? questionFromDB.getText() : questionReq.getText());
        questionFromDB.setAnswer(questionReq.getAnswer() == null ? questionFromDB.getAnswer() : questionReq.getAnswer());
        questionFromDB.setInfoSource(questionReq.getInfoSource() == null ? questionFromDB.getInfoSource() :
                questionReq.getInfoSource());
        questionFromDB.setType(questionReq.getType() == null ? questionFromDB.getType() : questionReq.getType());

        questionFromDB.setStatus(QuestionStatus.UPDATED);
        questionFromDB = questionRepository.save(questionFromDB);
        return objectMapper.convertValue(questionFromDB, QuestionInfoResp.class);
    }

    public void deleteQuestion(Long id) {
        QuestionInfo questionInfo = getQuestionFromDB(id);
        questionInfo.setStatus(QuestionStatus.DELETED);
        questionRepository.save(questionInfo);
    }

    public List<QuestionInfoResp> getAllQuestions() {
        return questionRepository.findAll().stream()
                .map(questionInfo -> objectMapper.convertValue(questionInfo, QuestionInfoResp.class))
                .collect(Collectors.toList());
    }

    public QuestionInfoResp linkTourAndQuestion(Long tourID, Long questionID) {
        Tour tourFromDB = tourService.getTourFromDB(tourID);
        QuestionInfo questionFromDB = getQuestionFromDB(questionID);

        if (questionFromDB.getId() == null || tourFromDB.getId() == null) {
            throw new CommonBackendException("Question or Tour is not found", HttpStatus.NOT_FOUND);
        }

        List<QuestionInfo> questions = tourFromDB.getQuestions();
        QuestionInfo addedQuestion = questions.stream().filter(questionInfo -> questionInfo.getId()
                .equals(questionID)).findFirst().orElse(null);

        if (addedQuestion != null) {
            throw new CommonBackendException("Question is already added", HttpStatus.CONFLICT);
        }

        questions.add(questionFromDB);
        Tour tour = tourService.updateTourQuestionsList(tourFromDB);

        questionFromDB.setTour(tour);
        questionFromDB.setStatus(QuestionStatus.PLAYED);
        questionRepository.save(questionFromDB);

        QuestionInfoResp questionInfoResp = objectMapper.convertValue(questionFromDB, QuestionInfoResp.class);
        TourInfoResp tourInfoResp = objectMapper.convertValue(tour, TourInfoResp.class);
        Game gameOfTour = tourFromDB.getGame();
        GameInfoResp gameInfoResp = objectMapper.convertValue(gameOfTour, GameInfoResp.class);

        tourInfoResp.setGame(gameInfoResp);
        questionInfoResp.setTour(tourInfoResp);

        return questionInfoResp;
    }
}
