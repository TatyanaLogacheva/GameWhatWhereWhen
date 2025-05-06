package com.example.GameWWW.service;

import com.example.GameWWW.exceptions.CommonBackendException;
import com.example.GameWWW.model.db.entity.AuthorOfQuestion;
import com.example.GameWWW.model.db.entity.QuestionInfo;
import com.example.GameWWW.model.db.repository.AuthorOfQuestionRepository;
import com.example.GameWWW.model.dto.request.AuthorOfQuestionReq;
import com.example.GameWWW.model.dto.responce.AuthorOfQuestionResp;
import com.example.GameWWW.model.dto.responce.QuestionInfoResp;
import com.example.GameWWW.model.enums.QuestionType;
import com.example.GameWWW.model.enums.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AuthorOfQuestionServiceTest {
    @InjectMocks
    private AuthorOfQuestionService authorService;

    @Mock
    private AuthorOfQuestionRepository authorRepository;

    @Spy
    private ObjectMapper objectMapper;

    @Test
    public void addAuthor() {
        AuthorOfQuestionReq authorReq = new AuthorOfQuestionReq();
        authorReq.setAuthorFirstName("Ivan");
        authorReq.setAuthorLastName("Ivanov");
        authorReq.setAuthorAge(35);
        authorReq.setEmail("ivan@mail.ru");

        AuthorOfQuestion author = new AuthorOfQuestion();
        author.setId(1L);
        author.setAuthorFirstName(authorReq.getAuthorFirstName());
        author.setAuthorLastName(authorReq.getAuthorLastName());
        author.setAuthorAge(authorReq.getAuthorAge());
        author.setEmail(authorReq.getEmail());

        when(authorRepository.save(any(AuthorOfQuestion.class))).thenReturn(author);
        AuthorOfQuestionResp authorResp = authorService.addAuthor(authorReq);
        assertEquals(author.getId(), authorResp.getId());
    }

    @Test(expected = CommonBackendException.class)
    public void addAuthorInvalidEmail() {
        AuthorOfQuestionReq req = new AuthorOfQuestionReq();
        req.setEmail("ivanmail.ru");

        authorService.addAuthor(req);
    }

    @Test(expected = CommonBackendException.class)
    public void addAuthorExisted() {
        AuthorOfQuestionReq req = new AuthorOfQuestionReq();
        req.setEmail("ivan@mail.ru");

        AuthorOfQuestion author = new AuthorOfQuestion();
        author.setId(1L);
        author.setEmail(req.getEmail());

        when(authorRepository.findAuthorOfQuestionByEmail(author.getEmail())).thenReturn(Optional.of(author));
        authorService.addAuthor(req);
    }

    @Test
    public void getAuthorFromDB() {
        AuthorOfQuestion author = new AuthorOfQuestion();
        author.setId(1L);
        author.setAuthorFirstName("Ivan");
        author.setAuthorLastName("Ivanov");
        author.setAuthorAge(35);
        author.setEmail("ivan@mail.ru");

        when(authorRepository.findById(author.getId())).thenReturn(Optional.of(author));
        AuthorOfQuestion authorFromBD = authorService.getAuthorFromDB(author.getId());

        assertEquals(authorFromBD.getEmail(), author.getEmail());
    }


    @Test(expected = CommonBackendException.class)
    public void getAuthorFromDBNotFound() {
        authorService.getAuthorFromDB(1L);
    }

    @Test
    public void getAuthor() {
        AuthorOfQuestion author = new AuthorOfQuestion();
        author.setId(1L);
        author.setAuthorFirstName("Ivan");
        author.setAuthorLastName("Ivanov");
        author.setAuthorAge(35);
        author.setEmail("ivan@mail.ru");

        when(authorRepository.findById(author.getId())).thenReturn(Optional.of(author));
        AuthorOfQuestionResp authorResp = authorService.getAuthor(author.getId());
        assertEquals(authorResp.getEmail(), author.getEmail());
    }

    @Test
    public void updateAuthor() {
        AuthorOfQuestion author = new AuthorOfQuestion();
        author.setId(1L);
        author.setAuthorFirstName("Ivan");
        author.setAuthorLastName("Ivanov");
        author.setAuthorAge(35);
        author.setEmail("ivan@mail.ru");

        AuthorOfQuestionReq authorReq = new AuthorOfQuestionReq();
        authorReq.setEmail("petrov@mail.ru");
        authorReq.setAuthorLastName("Petrov");

        when(authorRepository.findById(author.getId())).thenReturn(Optional.of(author));
        authorService.updateAuthor(author.getId(), authorReq);
        assertEquals(author.getEmail(), authorReq.getEmail());
        assertEquals(author.getAuthorLastName(), authorReq.getAuthorLastName());
    }

    @Test
    public void deleteAuthor() {
        AuthorOfQuestion author = new AuthorOfQuestion();
        author.setId(1L);
        when(authorRepository.findById(author.getId())).thenReturn(Optional.of(author));
        authorService.deleteAuthor(author.getId());
        verify(authorRepository, times(1)).save(any(AuthorOfQuestion.class));
        assertEquals(Status.DELETED, author.getStatus());
    }

    @Test
    public void getAllAuthor() {
        AuthorOfQuestion author1 = new AuthorOfQuestion();
        author1.setId(1L);
        author1.setAuthorFirstName("Ivan");
        author1.setAuthorLastName("Ivanov");
        author1.setAuthorAge(35);
        author1.setEmail("ivan@mail.ru");
        AuthorOfQuestion author2 = new AuthorOfQuestion();
        author2.setId(2L);
        author2.setAuthorFirstName("Petr");
        author2.setAuthorLastName("Petrov");
        author2.setAuthorAge(30);
        author2.setEmail("petr@mail.ru");
        List<AuthorOfQuestion> authors = Arrays.asList(author1, author2);
        when(authorRepository.findAll()).thenReturn(List.of(author1, author2));
        List<AuthorOfQuestionResp> authorsResp = authorService.getAllAuthor();
        assertEquals(authors.get(0).getId(), authorsResp.get(0).getId());
        assertEquals(authors.get(1).getId(), authorsResp.get(1).getId());
    }

    @Test
    public void updateQuestionsList() {
        AuthorOfQuestion author = new AuthorOfQuestion();
        author.setId(1L);
        authorService.updateQuestionsList(author);
        verify(authorRepository, times(1)).save(any(AuthorOfQuestion.class));
    }

    @Test
    public void getAllQuestions() {
        QuestionInfo question = new QuestionInfo();
        question.setId(1L);
        question.setText("text");
        question.setAnswer("answer");
        question.setInfoSource("net");
        question.setType(QuestionType.ORDINARY);
        List<QuestionInfo> questions = List.of(question);

        AuthorOfQuestion author = new AuthorOfQuestion();
        author.setId(1L);

        when(authorRepository.findById(author.getId())).thenReturn(Optional.of(author));
        author.setQuestions(questions);
        List<QuestionInfoResp> questResp = authorService.getAllQuestions(author.getId());
        assertEquals(questions.get(0).getText(), questResp.get(0).getText());
    }
}