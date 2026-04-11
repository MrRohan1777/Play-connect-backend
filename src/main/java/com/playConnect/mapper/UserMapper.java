package com.playConnect.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.playConnect.game.dto.CreateGameRequest;
import com.playConnect.game.entity.Game;
import com.playConnect.user.dto.RegisterRequest;
import com.playConnect.user.entity.User;

@Mapper
public interface UserMapper {

	UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
	
	User dtoToEntity(RegisterRequest request);
	RegisterRequest entityToDto(User userss);
	
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "status", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "hostCancelReason", ignore = true)
	@Mapping(target = "distance", ignore = true)
	@Mapping(target = "winnerId", ignore = true)
	@Mapping(target = "contactNumber", ignore = true)
	@Mapping(target = "email", ignore = true)
	@Mapping(target = "cancelBeforeMinutes", ignore = true)
	Game gameDtoToEntity(CreateGameRequest request);

	CreateGameRequest gameEntityToDto(Game game);
}
