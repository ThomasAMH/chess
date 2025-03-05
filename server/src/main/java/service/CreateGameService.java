package service;

import dataaccess.DataAccessDAO;
import dataaccess.DataAccessException;
import requests.CreateGameRequest;
import results.CreateGameResult;
import results.DataAccessResult;
import results.LogoutResult;

public class CreateGameService {
    public CreateGameResult createGame(CreateGameRequest request, DataAccessDAO dataService) {
        //Check of the token provided is active
        try {
            DataAccessResult daoResult = dataService.authData.doesAuthTokenExist(request.authToken());
            if (daoResult.data().equals("false")) {
                return new CreateGameResult(401, "Error: unauthorized", "");
            }
        } catch (DataAccessException e) {
            return new CreateGameResult(500, "Error: Unknown error detected in token query process.", "");
        }
        //Attempt to make the game
        try {
            DataAccessResult daoResult = dataService.gameData.createGame(request.gameName());
            return new CreateGameResult(200, "Request ok", request.gameName());

        } catch (DataAccessException e) {
            return new CreateGameResult(500, "Error: Unknown error detected in token query process.", "");
        }
    }
}
