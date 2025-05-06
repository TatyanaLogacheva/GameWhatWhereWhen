package com.example.GameWWW.service;

import com.example.GameWWW.exceptions.CommonBackendException;
import com.example.GameWWW.model.db.entity.Game;
import com.example.GameWWW.model.db.entity.Tour;
import com.example.GameWWW.model.db.repository.TourRepository;
import com.example.GameWWW.model.dto.request.TourInfoReq;
import com.example.GameWWW.model.dto.responce.TourInfoResp;
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
public class TourServiceTest {

    @InjectMocks
    private TourService tourService;

    @Mock
    private TourRepository tourRepository;
    @Mock
    private GameService gameService;

    @Spy
    private ObjectMapper mapper;

    @Test
    public void addTour() {
        Game game = new Game();
        game.setId(1L);
        when(gameService.getGameFromDB(game.getId())).thenReturn(game);
        List<Tour> tourList = new ArrayList<>();
        game.setTours(tourList);

        TourInfoReq req = new TourInfoReq();
        req.setTourNumber(2);
        req.setAmountOfQuestionsInTour(10);

        Tour tour = new Tour();
        tour.setId(1L);
        tour.setTourNumber(req.getTourNumber());
        tour.setAmountOfQuestionsInTour(req.getAmountOfQuestionsInTour());
        when(tourRepository.save(any(Tour.class))).thenReturn(tour);

        when(gameService.updateTourList(game)).thenReturn(game);
        TourInfoResp resp = tourService.addTour(req, game.getId());
        assertEquals(resp.getId(), tour.getId());
        assertEquals(resp.getGame().getId(), game.getId());
    }

    @Test
    public void getTourFromDB() {
        Tour tour = new Tour();
        tour.setId(1L);
        tour.setTourNumber(2);
        tour.setAmountOfQuestionsInTour(10);
        when(tourRepository.findById(tour.getId())).thenReturn(Optional.of(tour));

        Tour tourFromDB = tourService.getTourFromDB(tour.getId());
        assertEquals(tourFromDB.getAmountOfQuestionsInTour(), tour.getAmountOfQuestionsInTour());
    }

    @Test(expected = CommonBackendException.class)
    public void getTourFromDBNotFound() {
        tourService.getTourFromDB(1L);
    }

    @Test
    public void getTour() {
        Tour tour = new Tour();
        tour.setId(1L);
        tour.setTourNumber(2);
        tour.setAmountOfQuestionsInTour(10);
        when(tourRepository.findById(tour.getId())).thenReturn(Optional.of(tour));

        TourInfoResp resp = tourService.getTour(tour.getId());
        assertEquals(resp.getAmountOfQuestionsInTour(), tour.getAmountOfQuestionsInTour());
    }

    @Test
    public void updateTour() {
        Tour tour = new Tour();
        tour.setId(1L);
        tour.setTourNumber(2);
        tour.setAmountOfQuestionsInTour(10);
        when(tourRepository.findById(tour.getId())).thenReturn(Optional.of(tour));

        TourInfoReq req = new TourInfoReq();
        req.setTourNumber(1);
        req.setAmountOfQuestionsInTour(25);
        tourService.updateTour(tour.getId(), req);
        assertEquals(req.getTourNumber(), tour.getTourNumber());
        assertEquals(req.getAmountOfQuestionsInTour(),tour.getAmountOfQuestionsInTour());
    }

    @Test
    public void deleteTour() {
        Tour tour = new Tour();
        tour.setId(1L);
        when(tourRepository.findById(tour.getId())).thenReturn(Optional.of(tour));
        tourService.deleteTour(tour.getId());
        verify(tourRepository, times(1)).save(any(Tour.class));
        assertEquals(Status.DELETED, tour.getStatus());
    }

    @Test
    public void getAllTour() {
        Tour tour1 = new Tour();
        tour1.setId(1L);
        tour1.setTourNumber(1);
        tour1.setAmountOfQuestionsInTour(10);

        Tour tour2 = new Tour();
        tour2.setId(2L);
        tour2.setTourNumber(2);
        tour2.setAmountOfQuestionsInTour(35);

        List<Tour> tourList = Arrays.asList(tour1, tour2);
        when(tourRepository.findAll()).thenReturn(List.of(tour1, tour2));
        List<TourInfoResp> tourResp = tourService.getAllTour();
        assertEquals(tourResp.get(0).getId(), tourList.get(0).getId());
        assertEquals(tourResp.get(1).getId(), tourList.get(1).getId());
    }

    @Test
    public void updateTourQuestionsList() {
        Tour tour = new Tour();
        tour.setId(1L);
        tourService.updateTourQuestionsList(tour);
        verify(tourRepository, times(1)).save(any(Tour.class));
    }
}