package service;

import dataaccess.DataAccessDAO;
import dataaccess.DataAccessException;
import model.GameData;
import requests.ListGamesRequest;
import results.DataAccessResult;
import results.ListGamesResult;

import java.util.ArrayList;

public class ListGamesService {
    public ListGamesResult listGames(ListGamesRequest request, DataAccessDAO dataService) {
        //Validate auth token
        try {
            DataAccessResult daoResult = dataService.authData.doesAuthTokenExist(request.authToken());
            if (daoResult.data().equals("false")) {
                return new ListGamesResult(401, "Error: unauthorized", null);
            }
        } catch (DataAccessException e) {
            return new ListGamesResult(500, "Error: unknown error in token validation process", null);
        }

        //Return games
        try {
            ArrayList<GameData> daoResult = dataService.gameData.getGames();
            return new ListGamesResult(200, "Request successful", daoResult);

        } catch (DataAccessException e) {
            return new ListGamesResult(500, "Error: unknown error in token validation process", null);
        }
    }
}
