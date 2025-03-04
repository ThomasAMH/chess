package service;

import requests.CreateGameRequest;
import results.CreateGameResult;

public class CreateGameService {
    CreateGameResult createGame(CreateGameRequest request) {
        CreateGameResult result = new CreateGameResult(200, "test string");
        //Auth User
        //Make game
        return result;
    }
}
