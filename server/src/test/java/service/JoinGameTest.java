package service;
import com.google.gson.Gson;
import dataaccess.DataAccessDAO;
import dataaccess.MemoryDAO;
import org.junit.jupiter.api.Test;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import requests.LoginRequest;
import requests.RegisterRequest;
import results.CreateGameResult;
import results.JoinGameResult;
import results.RegisterResult;
import service.CreateGameService;
import service.JoinGameService;
import service.RegistrationService;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JoinGameTest {
    DataAccessDAO dataService = new MemoryDAO();
    RegisterRequest registerRequest = new RegisterRequest("Tom", "12345", "tom@thebomb.com");
    LoginRequest goodLoginRequest = new LoginRequest("Tom", "12345");
    LoginRequest badLoginRequest = new LoginRequest("Tom", "54321");

    RegistrationService regService = new RegistrationService();
    RegisterResult result1 = regService.registerUser(registerRequest, dataService);

    String goodToken = result1.authToken();
    String badToken = "12345";

    CreateGameService newGameService = new CreateGameService();
    CreateGameResult createGameResult = newGameService.createGame(new CreateGameRequest("Test Game", goodToken),dataService);

    JoinGameService joinGameService = new JoinGameService();
    JoinGameRequest badJoinGameRequest = new JoinGameRequest("WHITE", 0, "Tom", badToken);
    JoinGameRequest goodJoinGameRequest = new JoinGameRequest("WHITE", 1, "Tom", goodToken);

    @Test
    public void testBadRequest() {
        JoinGameResult result = joinGameService.joinGame(badJoinGameRequest, dataService);
        assertEquals(401, result.responseCode());
        System.out.print(new Gson().toJson(result, JoinGameResult.class));
    }

    @Test
    public void testGoodRequest() {
        JoinGameResult result = joinGameService.joinGame(goodJoinGameRequest, dataService);
        assertEquals(200, result.responseCode());
        System.out.print(new Gson().toJson(result, JoinGameResult.class));
    }

}