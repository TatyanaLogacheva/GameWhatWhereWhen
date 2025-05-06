package com.example.GameWWW.service;

import com.example.GameWWW.exceptions.CommonBackendException;
import com.example.GameWWW.model.db.entity.Game;
import com.example.GameWWW.model.db.entity.Tour;
import com.example.GameWWW.model.db.repository.TourRepository;
import com.example.GameWWW.model.dto.request.TourInfoReq;
import com.example.GameWWW.model.dto.responce.GameInfoResp;
import com.example.GameWWW.model.dto.responce.TourInfoResp;
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
public class TourService {
    private final ObjectMapper objectMapper;
    private final TourRepository tourRepository;
    private final GameService gameService;

    public TourInfoResp addTour(TourInfoReq req, Long gameID) {
        Game gameFromDB = gameService.getGameFromDB(gameID);
        Tour tour = objectMapper.convertValue(req, Tour.class);

        tour.setStatus(Status.CREATED);
        Tour saveTour = tourRepository.save(tour);

        List<Tour> tours = gameFromDB.getTours();
        tours.add(saveTour);
        Game updateGame = gameService.updateTourList(gameFromDB);

        saveTour.setGame(updateGame);
        tourRepository.save(saveTour);

        GameInfoResp gameInfoResp = objectMapper.convertValue(updateGame, GameInfoResp.class);
        TourInfoResp tourInfoResp = objectMapper.convertValue(saveTour, TourInfoResp.class);
        tourInfoResp.setGame(gameInfoResp);
        return tourInfoResp;
    }

    public Tour getTourFromDB(Long id) {
        Optional<Tour> optionalTour = tourRepository.findById(id);
        final String errorMsg = String.format("Tour with id %d is not found", id);
        return optionalTour.orElseThrow(() -> new CommonBackendException(errorMsg, HttpStatus.NOT_FOUND));
    }

    public TourInfoResp getTour(Long id) {
        Tour tour = getTourFromDB(id);
        return objectMapper.convertValue(tour, TourInfoResp.class);
    }

    public TourInfoResp updateTour(Long id, TourInfoReq req) {
        Tour tourFromDB = getTourFromDB(id);
        Tour tourReq = objectMapper.convertValue(req, Tour.class);

        tourFromDB.setTourNumber(tourReq.getTourNumber() == null ? tourFromDB.getTourNumber() : tourReq.getTourNumber());
        tourFromDB.setAmountOfQuestionsInTour(tourReq.getAmountOfQuestionsInTour() == null ?
                tourFromDB.getAmountOfQuestionsInTour() : tourReq.getAmountOfQuestionsInTour());
        tourFromDB.setStatus(Status.UPDATED);

        tourFromDB = tourRepository.save(tourFromDB);
        return objectMapper.convertValue(tourFromDB, TourInfoResp.class);
    }

    public void deleteTour(Long id) {
        Tour tour = getTourFromDB(id);
        tour.setStatus(Status.DELETED);
        tourRepository.save(tour);
    }

    public List<TourInfoResp> getAllTour() {
        return tourRepository.findAll().stream()
                .map(tour -> objectMapper.convertValue(tour, TourInfoResp.class))
                .collect(Collectors.toList());
    }

    public Tour updateTourQuestionsList(Tour updateTour) {
        return tourRepository.save(updateTour);
    }
}
