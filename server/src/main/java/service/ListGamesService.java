package service;

import chess.ChessGame;
import dataaccess.DataAccessDAO;
import dataaccess.DataAccessException;
import requests.ListGamesRequest;
import results.DataAccessResult;
import results.ListGamesResult;
import results.LogoutResult;
import java.util.ArrayList;

public class ListGamesService {
    public ListGamesResult listGames(ListGamesRequest request, DataAccessDAO dataService) {
        //Validate auth token
        try {
            DataAccessResult daoResult = dataService.authData.doesAuthTokenExist(request.authToken());
            if (daoResult.data().equals("false")) {
                return new ListGamesResult(401, "Error: unauthorized", "");
            }
        } catch (DataAccessException e) {
            return new ListGamesResult(500, "Error: unknown error in token validation process", "");
        }

        //Return games
        try {
            DataAccessResult daoResult = dataService.gameData.getGames();
            if (daoResult.data().equals("false")) {
                return new ListGamesResult(401, "Error: unauthorized", "");
            } else {
                return new ListGamesResult(200, "Request successful", daoResult.data());
            }
        } catch (DataAccessException e) {
            return new ListGamesResult(500, "Error: unknown error in token validation process", "");
        }
    }
}
