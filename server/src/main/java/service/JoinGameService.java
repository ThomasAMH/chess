package service;

import requests.JoinGameRequest;
import results.JoinGameResult;

public class JoinGameService {
    JoinGameResult joinGame(JoinGameRequest request) {
        JoinGameResult result = new JoinGameResult(200, "test");
        //auth user
        //Check availability
        return result;
    }
}
