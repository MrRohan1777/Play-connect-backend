package com.playConnect.game.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.playConnect.game.entity.Game;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

	@Query("""
			    SELECT g,
			    (
			        6371 * acos(
			            cos(radians(:lat)) *
			            cos(radians(g.latitude)) *
			            cos(radians(g.longitude) - radians(:lng)) +
			            sin(radians(:lat)) *
			            sin(radians(g.latitude))
			        )
			    ) AS distance
			    FROM Game g
			    WHERE g.remainingSpots > 0
			    AND g.status = 'ACTIVE'
			    AND (
			        g.date > CURRENT_DATE
			        OR (g.date = CURRENT_DATE AND g.time > CURRENT_TIME)
			    )
			    AND (
			        6371 * acos(
			            cos(radians(:lat)) *
			            cos(radians(g.latitude)) *
			            cos(radians(g.longitude) - radians(:lng)) +
			            sin(radians(:lat)) *
			            sin(radians(g.latitude))
			        )
			    ) <= :radius
			    ORDER BY distance
			""")
	List<Object[]> findNearbyGames(@Param("lat") double lat, @Param("lng") double lng, @Param("radius") double radius);
	
	List<Game> findByHostId(Long hostId);

}
