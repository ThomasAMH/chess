package service;

import dataaccess.DataAccessDAO;
import dataaccess.DataAccessException;
import requests.JoinGameRequest;
import results.DataAccessResult;
import results.JoinGameResult;

public class JoinGameService {
    public JoinGameResult joinGame(JoinGameRequest request, DataAccessDAO dataService) {
        //Check of the token provided is active
        try {
            DataAccessResult daoResult = dataService.authData.doesAuthTokenExist(request.authToken());
            if (daoResult.data().equals("false")) {
                return new JoinGameResult(401, "Error: unauthorized");
            }
        } catch (DataAccessException e) {
            return new JoinGameResult(500, "Error: Unknown error detected in token query process.");
        }

        //Check game number validity
        try {
            DataAccessResult daoResult = dataService.gameData.isGameNumberValid(request.gameID());
            if (daoResult.data().equals("false")) {
                return new JoinGameResult(400, "Error: bad request. Invalid game number");
            }
        } catch (DataAccessException e) {
            return new JoinGameResult(500, "Error: Unknown error detected in game number query process.");
        }

        //Is color available
        try {
            DataAccessResult daoResult = dataService.gameData.isColorAvailable(request.gameID(), request.playerColor());
            if (daoResult.data().equals("false")) {
                return new JoinGameResult(403, "Error: already taken");
            }
        } catch (DataAccessException e) {
            return new JoinGameResult(500, "Error: Unknown error detected in color checking process.");
        }

        //Join game
        try {
            DataAccessResult daoResult = dataService.gameData.joinGame(request.gameID(), request.playerColor(), request.authToken());
            return new JoinGameResult(200, "Game joined successfully");
        } catch (DataAccessException e) {
            return new JoinGameResult(500, "Error: Unknown error detected in color checking process.");
        }
    }
}
