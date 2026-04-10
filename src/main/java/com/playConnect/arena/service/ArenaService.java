package com.playConnect.arena.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.playConnect.arena.dto.ArenaResponse;
import com.playConnect.arena.entity.Arena;
import com.playConnect.arena.repository.ArenaRepository;

@Service
public class ArenaService {

	@Autowired
	private ArenaRepository arenaRepository;

	public List<ArenaResponse> getNearbyArenas(double userLat, double userLng, double radius) {
		return arenaRepository.findAll().stream()
				.map(arena -> {
					double distance = round2(calculateDistance(userLat, userLng, arena.getLatitude(), arena.getLongitude()));
					return new ArenaResponse(arena, distance);
				})
				.filter(arena -> arena.getDistance() <= radius)
				.sorted(Comparator.comparingDouble(ArenaResponse::getDistance))
				.toList();
	}

	private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
		int radiusKm = 6371;
		double latDistance = Math.toRadians(lat2 - lat1);
		double lonDistance = Math.toRadians(lon2 - lon1);
		double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
				+ Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
						* Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		return radiusKm * c;
	}

	private double round2(double value) {
		return Math.round(value * 100.0d) / 100.0d;
	}
}
