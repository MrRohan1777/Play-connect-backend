package com.playConnect.arena.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.playConnect.arena.dto.ArenaResponse;
import com.playConnect.arena.service.ArenaService;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/arenas")
public class ArenaController {

	@Autowired
	private ArenaService arenaService;

	@GetMapping
	public List<ArenaResponse> getNearbyArenas(
			@RequestParam double lat,
			@RequestParam double lng,
			@RequestParam(defaultValue = "5") double radius) {
		return arenaService.getNearbyArenas(lat, lng, radius);
	}
}
