package com.example.GameWWW.controllers;

import com.example.GameWWW.model.dto.request.TeamAnswerInfoReq;
import com.example.GameWWW.model.dto.responce.TeamAnswerInfoResp;
import com.example.GameWWW.service.TeamAnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/teamAnswers")
public class TeamAnswerController {
    private final TeamAnswerService teamAnswerService;

    @PostMapping("/{teamID}/{questionID}")
    public TeamAnswerInfoResp addTeamAnswer (@RequestBody TeamAnswerInfoReq req, @PathVariable Long teamID,
                                             @PathVariable Long questionID){
        return teamAnswerService.addTeamAnswer(req, teamID, questionID);
    }
    @GetMapping("/{id}")
    public TeamAnswerInfoResp getTeamAnswer (@PathVariable Long id){
        return teamAnswerService.getTeamAnswer(id);
    }

    @PutMapping("/{id}")
    public TeamAnswerInfoResp updateTeamAnswer (@PathVariable Long id, @RequestBody TeamAnswerInfoReq req){
        return teamAnswerService.updateTeamAnswer(id, req);
    }

    @DeleteMapping("/{id}")
    public void deleteTeamAnswer (@PathVariable Long id) {
        teamAnswerService.deleteTeamAnswer(id);
    }

    @GetMapping
    public List<TeamAnswerInfoResp> getAllTeamAnswers () {
        return teamAnswerService.getAllTeamAnswers();
    }
}
