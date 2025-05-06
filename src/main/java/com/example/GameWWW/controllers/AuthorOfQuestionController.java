package com.example.GameWWW.controllers;

import com.example.GameWWW.model.dto.request.AuthorOfQuestionReq;
import com.example.GameWWW.model.dto.responce.AuthorOfQuestionResp;
import com.example.GameWWW.model.dto.responce.QuestionInfoResp;
import com.example.GameWWW.service.AuthorOfQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/authors")
public class AuthorOfQuestionController {

    private final AuthorOfQuestionService authorOfQuestionService;

    @PostMapping
    public AuthorOfQuestionResp addAuthor(@RequestBody @Valid AuthorOfQuestionReq req){
        return authorOfQuestionService.addAuthor(req);
    }

    @GetMapping("/{id}")
    public AuthorOfQuestionResp getAuthor(@PathVariable Long id){
        return authorOfQuestionService.getAuthor(id);
    }

    @PutMapping("/{id}")
    public AuthorOfQuestionResp updateAuthor(@PathVariable Long id, @RequestBody AuthorOfQuestionReq req){
        return authorOfQuestionService.updateAuthor(id, req);
    }

    @DeleteMapping ("/{id}")
    public void deleteAuthor(@PathVariable Long id){
        authorOfQuestionService.deleteAuthor(id);
    }

    @GetMapping
    public List<AuthorOfQuestionResp> getAllAuthor() {
        return authorOfQuestionService.getAllAuthor();
    }

    @GetMapping("/allQuestions/{id}")
    public List<QuestionInfoResp> getAllQuestions(@PathVariable Long id){
        return authorOfQuestionService.getAllQuestions(id);
    }
}
