package com.playConnect.mapper;

import com.playConnect.game.dto.CreateGameRequest;
import com.playConnect.game.entity.Game;
import com.playConnect.user.dto.RegisterRequest;
import com.playConnect.user.entity.User;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-11T14:48:14+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.18 (Oracle Corporation)"
)
public class UserMapperImpl implements UserMapper {

    @Override
    public User dtoToEntity(RegisterRequest request) {
        if ( request == null ) {
            return null;
        }

        User user = new User();

        user.setName( request.getName() );
        user.setEmail( request.getEmail() );
        user.setPassword( request.getPassword() );
        user.setSkillLevel( request.getSkillLevel() );

        return user;
    }

    @Override
    public RegisterRequest entityToDto(User userss) {
        if ( userss == null ) {
            return null;
        }

        RegisterRequest registerRequest = new RegisterRequest();

        registerRequest.setName( userss.getName() );
        registerRequest.setEmail( userss.getEmail() );
        registerRequest.setPassword( userss.getPassword() );
        registerRequest.setSkillLevel( userss.getSkillLevel() );

        return registerRequest;
    }

    @Override
    public Game gameDtoToEntity(CreateGameRequest request) {
        if ( request == null ) {
            return null;
        }

        Game game = new Game();

        game.setArenaId( request.getArenaId() );
        game.setSport( request.getSport() );
        game.setLatitude( request.getLatitude() );
        game.setLongitude( request.getLongitude() );
        game.setStartTime( request.getStartTime() );
        game.setTotalPlayers( request.getTotalPlayers() );

        return game;
    }

    @Override
    public CreateGameRequest gameEntityToDto(Game game) {
        if ( game == null ) {
            return null;
        }

        CreateGameRequest createGameRequest = new CreateGameRequest();

        createGameRequest.setSport( game.getSport() );
        createGameRequest.setArenaId( game.getArenaId() );
        createGameRequest.setLatitude( game.getLatitude() );
        createGameRequest.setLongitude( game.getLongitude() );
        createGameRequest.setStartTime( game.getStartTime() );
        createGameRequest.setTotalPlayers( game.getTotalPlayers() );

        return createGameRequest;
    }
}
