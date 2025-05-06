package com.example.GameWWW.controllers;

import com.example.GameWWW.model.dto.request.TourInfoReq;
import com.example.GameWWW.model.dto.responce.TourInfoResp;
import com.example.GameWWW.service.TourService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tours")
public class TourController {
    private final TourService tourService;

    @PostMapping("/{gameID}")
    public TourInfoResp addTour (@RequestBody TourInfoReq req, @PathVariable Long gameID){
        return tourService.addTour(req, gameID);
    }
    @GetMapping ("/{id}")
    public TourInfoResp getTour(@PathVariable Long id){
        return tourService.getTour(id);
    }

    @PutMapping ("/{id}")
    public TourInfoResp updateTour(@PathVariable Long id, @RequestBody TourInfoReq req){
        return tourService.updateTour(id, req);
    }

    @DeleteMapping ("/{id}")
    public void deleteTour(@PathVariable Long id){
        tourService.deleteTour(id);
    }

    @GetMapping
    public List<TourInfoResp> getAllTour() {
        return tourService.getAllTour();
    }
}
