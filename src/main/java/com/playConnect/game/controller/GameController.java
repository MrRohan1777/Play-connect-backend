package com.playConnect.game.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.playConnect.Response.ApiResponse;
import com.playConnect.exception.BadRequestException;
import com.playConnect.game.dto.CreateGameRequest;
import com.playConnect.game.dto.CreateGameResponse;
import com.playConnect.game.dto.GameListResponse;
import com.playConnect.game.service.GameService;
import com.playConnect.utilities.AppConstants;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/games")
public class GameController {

    @Autowired
    private GameService gameService;

    /**
     * Host identity comes from JWT only ({@code Authorization}), never from the request body.
     */
    @PostMapping
    public ResponseEntity<CreateGameResponse> createGame(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody CreateGameRequest request) {

        CreateGameResponse body = gameService.createGame(token, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<GameListResponse>> getGames(
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng,
            @RequestParam(required = false) Double radius,
            @RequestParam(required = false) Long arenaId,
            @RequestParam(required = false) String sport) {

        GameListResponse gameListResponse;
        if (arenaId != null) {
            gameListResponse = gameService.getGamesByArena(arenaId, lat, lng);
        } else {
            if (lat == null || lng == null || radius == null) {
                throw new BadRequestException("lat, lng and radius are required when arenaId is not provided");
            }
            gameListResponse = gameService.getNearbyGames(lat, lng, radius, sport);
        }
        ApiResponse<GameListResponse> response = new ApiResponse<>();
        response.setMessage("Games fetched successfully");
        response.setData(gameListResponse);
        response.setStatus(AppConstants.SUCCESS);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filling-fast")
    public ResponseEntity<ApiResponse<GameListResponse>> getFillingFastGames(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam double radius) {

        GameListResponse gameListResponse = gameService.getFillingFastGames(lat, lng, radius);
        ApiResponse<GameListResponse> response = new ApiResponse<>();
        response.setMessage("Filling fast games");
        response.setData(gameListResponse);
        response.setStatus(AppConstants.SUCCESS);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{gameId}/join")
    public ResponseEntity<ApiResponse<Long>> joinGame(
            @PathVariable Long gameId,
            @RequestHeader("Authorization") String token) {
        Long participationId = gameService.joinGame(gameId, token);
        ApiResponse<Long> response = new ApiResponse<>();
        response.setMessage("Joined game successfully");
        response.setData(participationId);
        response.setStatus(AppConstants.SUCCESS);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{gameId}/leave")
    public ResponseEntity<ApiResponse<String>> leaveGame(
            @PathVariable Long gameId,
            @RequestHeader("Authorization") String token) {
        gameService.leaveGame(gameId, token);
        ApiResponse<String> response = new ApiResponse<>();
        response.setMessage("Left game successfully");
        response.setData("OK");
        response.setStatus(AppConstants.SUCCESS);
        return ResponseEntity.ok(response);
    }
}