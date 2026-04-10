package com.playConnect.mapper;

import org.mapstruct.Mapper;
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
	
	Game gameDtoToEntity(CreateGameRequest request);
	CreateGameRequest gameEntityToDto(Game game);
}
