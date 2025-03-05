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
                return new CreateGameResult(401, "Error: unauthorized", -1);
            }
        } catch (DataAccessException e) {
            return new CreateGameResult(500, "Error: Unknown error detected in token query process.", -1);
        }
        //Attempt to make the game
        try {
            int gameID = dataService.gameData.createGame(request.gameName());
            return new CreateGameResult(200, "Request ok", gameID);

        } catch (DataAccessException e) {
            return new CreateGameResult(500, "Error: Unknown error detected in token query process.", -1);
        }
    }
}
