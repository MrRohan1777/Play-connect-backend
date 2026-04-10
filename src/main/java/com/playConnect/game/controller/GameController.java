package com.playConnect.game.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.playConnect.Response.ApiResponse;
import com.playConnect.game.dto.CreateGameRequest;
import com.playConnect.game.dto.JoinedGameResponse;
import com.playConnect.game.dto.MyGameItemResponse;
import com.playConnect.game.dto.MyGameResponse;
import com.playConnect.game.dto.NearbyGamesResponse;
import com.playConnect.game.dto.ParticipateGameRequest;
import com.playConnect.game.dto.RemovePlayerRequest;
import com.playConnect.game.entity.Game;
import com.playConnect.game.service.GameService;
import com.playConnect.utilities.AppConstants;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/games")
public class GameController {

    @Autowired
    private GameService gameService;

    @PostMapping("/host")
    public ResponseEntity<ApiResponse<Game>> hostGame(
    		@RequestHeader("Authorization") String token,
            @RequestBody CreateGameRequest request) {

        Game game = gameService.hostGame(token, request);
        ApiResponse<Game> response = new ApiResponse<>();
		response.setMessage("Game Created Successfully...!");
		response.setData(game);
		response.setStatus(AppConstants.SUCCESS);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/nearbyGames")
    public ResponseEntity<ApiResponse<NearbyGamesResponse>> getNearbyGames(
            @RequestParam String sport,
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam double radius) {

        NearbyGamesResponse nearByGameresponse =
                gameService.getNearbyGames(sport, lat, lng, radius);
        ApiResponse<NearbyGamesResponse> response = new ApiResponse<>();
		response.setMessage("Nearby Games...!");
		response.setData(nearByGameresponse);
		response.setStatus(AppConstants.SUCCESS);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/join/{gameId}")
    public ResponseEntity<ApiResponse<Long>> joinGame(
            @PathVariable Long gameId,
            @RequestBody ParticipateGameRequest request,
            @RequestHeader("Authorization") String token) {

        Long participationId = gameService.joinGame(gameId, request, token);
        ApiResponse<Long> response = new ApiResponse<>();
		response.setMessage("You Joined Game Successfully...!");
		response.setData(participationId);
		response.setStatus(AppConstants.SUCCESS);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    
    @PatchMapping("/cancelGame/{gameId}")
    public ResponseEntity<ApiResponse<String>> cancelGame(
            @PathVariable Long gameId,
            @RequestBody RemovePlayerRequest request,
            @RequestHeader("Authorization") String token) {

        String cancelGameResponse = gameService.cancelGame(gameId, token,request);
        ApiResponse<String> response = new ApiResponse<>();
		response.setMessage("Game Canceled...!");
		response.setData(cancelGameResponse);
		response.setStatus(AppConstants.SUCCESS);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    
    @PatchMapping("/leaveGame/{participationId}")
    public ResponseEntity<ApiResponse<String>> leaveGame(
            @PathVariable Long participationId,
            @RequestBody RemovePlayerRequest request,
            @RequestHeader("Authorization") String token) {

        String leavGameResponse = gameService.leaveGame(participationId, token,request);
        ApiResponse<String> response = new ApiResponse<>();
		response.setMessage("You Left The Game...!");
		response.setData(leavGameResponse);
		response.setStatus(AppConstants.SUCCESS);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    
    @PatchMapping("/remove/{gameId}/{participationId}")
    public ResponseEntity<ApiResponse<String>> removePlayer(
            @PathVariable Long gameId,
            @PathVariable Long participationId,
            @RequestBody RemovePlayerRequest request,
            @RequestHeader("Authorization") String token) {

        String removePlayerResponse = gameService.removePlayer(gameId, participationId, token, request);
        ApiResponse<String> response = new ApiResponse<>();
		response.setMessage("Player Kiked Out...!");
		response.setData(removePlayerResponse);
		response.setStatus(AppConstants.SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    
    @GetMapping("/myGames")
    public ResponseEntity<ApiResponse<MyGameResponse>> getAllGames(
    		@RequestHeader("Authorization") String token) {

    	MyGameResponse getMyGameresponse =
                gameService.getMyGames(token);
        ApiResponse<MyGameResponse> response = new ApiResponse<>();
		response.setMessage("Nearby Games...!");
		response.setData(getMyGameresponse);
		response.setStatus(AppConstants.SUCCESS);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    
    @GetMapping("/myJoinedGames")
    public ResponseEntity<ApiResponse<List<JoinedGameResponse>>> myJoinedGames(
    		@RequestHeader("Authorization") String token) {

    	List<JoinedGameResponse> getMyGameresponse =
                gameService.getJoinedGames(token);
        ApiResponse<List<JoinedGameResponse>> response = new ApiResponse<>();
		response.setMessage("Joined Games...!");
		response.setData(getMyGameresponse);
		response.setStatus(AppConstants.SUCCESS);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/user/{userId}/hosted")
    public ResponseEntity<ApiResponse<List<MyGameItemResponse>>> getHostedGamesByUserId(
            @PathVariable Long userId) {

        List<MyGameItemResponse> hosted = gameService.getHostedGamesByUserId(userId);
        ApiResponse<List<MyGameItemResponse>> response = new ApiResponse<>();
        response.setMessage("User hosted games");
        response.setData(hosted);
        response.setStatus(AppConstants.SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/user/{userId}/joined")
    public ResponseEntity<ApiResponse<List<JoinedGameResponse>>> getJoinedGamesByUserId(
            @PathVariable Long userId) {

        List<JoinedGameResponse> joined = gameService.getJoinedGamesByUserId(userId);
        ApiResponse<List<JoinedGameResponse>> response = new ApiResponse<>();
        response.setMessage("User joined games");
        response.setData(joined);
        response.setStatus(AppConstants.SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}