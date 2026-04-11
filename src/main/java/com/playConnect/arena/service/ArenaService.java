package com.playConnect.arena.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.playConnect.arena.dto.ArenaDetailResponse;
import com.playConnect.arena.dto.ArenaPageResponse;
import com.playConnect.arena.dto.ArenaResponse;
import com.playConnect.arena.entity.Arena;
import com.playConnect.arena.repository.ArenaRepository;
import com.playConnect.exception.ResourceNotFoundException;

@Service
public class ArenaService {

	private static final int MAX_PAGE_SIZE = 100;
	private static final int DEFAULT_PAGE_SIZE = 20;

	@Autowired
	private ArenaRepository arenaRepository;

	@Value("${app.arena.fetch-limit:2000}")
	private int arenaFetchLimit;

	public ArenaPageResponse getNearbyArenas(double userLat, double userLng, double radius, int page, int size) {
		int cappedSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
		int safePage = Math.max(page, 0);
		int fetch = Math.min(Math.max(arenaFetchLimit, 1), 10_000);

		Page<Arena> batch = arenaRepository.findAll(PageRequest.of(0, fetch, Sort.by("id")));

		List<ArenaResponse> filtered = batch.getContent().stream()
				.map(arena -> {
					if (arena.getLatitude() == null || arena.getLongitude() == null) {
						return new ArenaResponse(arena, Double.POSITIVE_INFINITY);
					}
					double distance = round2(calculateDistance(userLat, userLng, arena.getLatitude(), arena.getLongitude()));
					return new ArenaResponse(arena, distance);
				})
				.filter(a -> a.getDistance() <= radius)
				.sorted(Comparator.comparingDouble(ArenaResponse::getDistance))
				.toList();

		long totalElements = filtered.size();
		int from = safePage * cappedSize;
		List<ArenaResponse> slice = from >= filtered.size()
				? List.of()
				: filtered.subList(from, Math.min(from + cappedSize, filtered.size()));

		return new ArenaPageResponse(slice, safePage, cappedSize, totalElements);
	}

	public ArenaDetailResponse getArenaById(Long id) {
		Arena arena = arenaRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Arena not found"));
		return new ArenaDetailResponse(arena);
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
