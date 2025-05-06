package com.example.GameWWW.controllers;

import com.example.GameWWW.model.dto.request.QuestionInfoReq;
import com.example.GameWWW.model.dto.responce.QuestionInfoResp;
import com.example.GameWWW.service.QuestionInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/questionsInfo")
public class QuestionInfoController {
    private final QuestionInfoService questionInfoService;

    @PostMapping("/{authorID}")
    public QuestionInfoResp addQuestion(@RequestBody QuestionInfoReq req, @PathVariable Long authorID){
        return questionInfoService.addQuestion(req, authorID);
    }

    @GetMapping("/{id}")
    public QuestionInfoResp getQuestion(@PathVariable Long id){
        return questionInfoService.getQuestion(id);
    }

    @PutMapping("/{id}")
    public QuestionInfoResp updateQuestion(@PathVariable Long id, @RequestBody QuestionInfoReq req){
        return questionInfoService.updateQuestion(id, req);
    }
    @DeleteMapping("/{id}")
    public void deleteQuestion(@PathVariable Long id){
        questionInfoService.deleteQuestion(id);
    }

    @GetMapping
    public List<QuestionInfoResp> getAllQuestions(){
        return questionInfoService.getAllQuestions();
    }

    @PostMapping("linkTourAndQuestion/{tourID}/{questionID}")
    public QuestionInfoResp linkTourAndQuestion (@PathVariable Long tourID, @PathVariable Long questionID){
        return questionInfoService.linkTourAndQuestion(tourID,questionID);
    }
}
