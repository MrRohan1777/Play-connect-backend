package com.playConnect.arena.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.playConnect.Response.ApiResponse;
import com.playConnect.arena.dto.ArenaDetailResponse;
import com.playConnect.arena.dto.ArenaPageResponse;
import com.playConnect.arena.service.ArenaService;
import com.playConnect.utilities.AppConstants;

@RestController
@RequestMapping("/arenas")
public class ArenaController {

	@Autowired
	private ArenaService arenaService;

	@GetMapping
	public ResponseEntity<ApiResponse<ArenaPageResponse>> getNearbyArenas(
			@RequestParam double lat,
			@RequestParam double lng,
			@RequestParam(defaultValue = "5") double radius,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {
		ArenaPageResponse data = arenaService.getNearbyArenas(lat, lng, radius, page, size);
		ApiResponse<ArenaPageResponse> response = new ApiResponse<>();
		response.setMessage("Nearby arenas");
		response.setData(data);
		response.setStatus(AppConstants.SUCCESS);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<ArenaDetailResponse>> getArenaById(@PathVariable Long id) {
		ArenaDetailResponse arena = arenaService.getArenaById(id);
		ApiResponse<ArenaDetailResponse> response = new ApiResponse<>();
		response.setMessage("Arena details");
		response.setData(arena);
		response.setStatus(AppConstants.SUCCESS);
		return ResponseEntity.ok(response);
	}
}
