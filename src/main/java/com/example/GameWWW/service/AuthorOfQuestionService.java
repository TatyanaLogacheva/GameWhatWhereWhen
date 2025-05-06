package com.example.GameWWW.service;

import com.example.GameWWW.exceptions.CommonBackendException;
import com.example.GameWWW.model.db.entity.AuthorOfQuestion;
import com.example.GameWWW.model.db.entity.QuestionInfo;
import com.example.GameWWW.model.db.repository.AuthorOfQuestionRepository;
import com.example.GameWWW.model.dto.request.AuthorOfQuestionReq;
import com.example.GameWWW.model.dto.responce.AuthorOfQuestionResp;
import com.example.GameWWW.model.dto.responce.QuestionInfoResp;
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
public class AuthorOfQuestionService {
    private final ObjectMapper objectMapper;
    private final AuthorOfQuestionRepository authorOfQuestionRepository;

    public AuthorOfQuestionResp addAuthor(AuthorOfQuestionReq req) {
        if (!EmailValidator.getInstance().isValid(req.getEmail())) {
            throw new CommonBackendException("Email is invalid", HttpStatus.BAD_REQUEST);
        }
        authorOfQuestionRepository.findAuthorOfQuestionByEmail(req.getEmail())
                .ifPresent(authorOfQuestion -> {
                    throw new CommonBackendException("Author already exists", HttpStatus.CONFLICT);
                });
        AuthorOfQuestion authorOfQuestion = objectMapper.convertValue(req, AuthorOfQuestion.class);
        authorOfQuestion.setStatus(Status.CREATED);
        AuthorOfQuestion save = authorOfQuestionRepository.save(authorOfQuestion);
        return objectMapper.convertValue(save, AuthorOfQuestionResp.class);
    }

    public AuthorOfQuestion getAuthorFromDB(Long id) {
        Optional<AuthorOfQuestion> optionalAuthorOfQuestion = authorOfQuestionRepository.findById(id);
        final String errorMessage = String.format("Author with id %s is not found", id);
        return optionalAuthorOfQuestion.orElseThrow(() -> new CommonBackendException(errorMessage, HttpStatus.NOT_FOUND));
    }

    public AuthorOfQuestionResp getAuthor(Long id) {
        AuthorOfQuestion authorOfQuestion = getAuthorFromDB(id);
        return objectMapper.convertValue(authorOfQuestion, AuthorOfQuestionResp.class);
    }

    public AuthorOfQuestionResp updateAuthor(Long id, AuthorOfQuestionReq req) {
        if (!EmailValidator.getInstance().isValid(req.getEmail())) {
            throw new CommonBackendException("Email is invalid", HttpStatus.BAD_REQUEST);
        }
        AuthorOfQuestion authorFromDB = getAuthorFromDB(id);
        AuthorOfQuestion authorReq = objectMapper.convertValue(req, AuthorOfQuestion.class);

        authorFromDB.setAuthorFirstName(authorReq.getAuthorFirstName() == null ? authorFromDB.getAuthorFirstName() :
                authorReq.getAuthorFirstName());
        authorFromDB.setAuthorLastName(authorReq.getAuthorLastName() == null ? authorFromDB.getAuthorLastName() :
                authorReq.getAuthorLastName());
        authorFromDB.setAuthorMiddleName(authorReq.getAuthorMiddleName() == null ? authorFromDB.getAuthorMiddleName() :
                authorReq.getAuthorMiddleName());
        authorFromDB.setAuthorAge(authorReq.getAuthorAge() == null ? authorFromDB.getAuthorAge() : authorReq.getAuthorAge());
        authorFromDB.setEmail(authorReq.getEmail() == null ? authorFromDB.getEmail() : authorReq.getEmail());

        authorFromDB.setStatus(Status.UPDATED);
        authorFromDB = authorOfQuestionRepository.save(authorFromDB);
        return objectMapper.convertValue(authorFromDB, AuthorOfQuestionResp.class);
    }

    public void deleteAuthor(Long id) {
        AuthorOfQuestion authorOfQuestion = getAuthorFromDB(id);
        authorOfQuestion.setStatus(Status.DELETED);
        authorOfQuestionRepository.save(authorOfQuestion);
    }

    public List<AuthorOfQuestionResp> getAllAuthor() {
        return authorOfQuestionRepository.findAll().stream()
                .map(author -> objectMapper.convertValue(author, AuthorOfQuestionResp.class))
                .collect(Collectors.toList());
    }

    public AuthorOfQuestion updateQuestionsList(AuthorOfQuestion updatedAuthor) {
        return authorOfQuestionRepository.save(updatedAuthor);
    }

    public List<QuestionInfoResp> getAllQuestions(Long id) {
        AuthorOfQuestion author = getAuthorFromDB(id);
        List<QuestionInfo> questions = author.getQuestions();
        return questions.stream().map(question -> objectMapper.convertValue(question, QuestionInfoResp.class))
                .collect(Collectors.toList());
    }
}
