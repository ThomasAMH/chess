package service;

import com.google.gson.Gson;
import dataaccess.DataAccessDAO;
import dataaccess.MemoryDAO;
import org.junit.jupiter.api.Test;
import requests.CreateGameRequest;
import requests.ListGamesRequest;
import requests.LoginRequest;
import requests.RegisterRequest;
import results.CreateGameResult;
import results.ListGamesResult;
import results.RegisterResult;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GetGamesTest {
    DataAccessDAO dataService = new MemoryDAO();
    RegisterRequest registerRequest = new RegisterRequest("Tom", "12345", "tom@thebomb.com");
    LoginRequest goodLoginRequest = new LoginRequest("Tom", "12345");
    LoginRequest badLoginRequest = new LoginRequest("Tom", "54321");
    RegistrationService service;

    RegistrationService regService = new RegistrationService();
    RegisterResult result1 = regService.registerUser(registerRequest, dataService);

    String goodToken = result1.authToken();
    String badToken = "12345";

    CreateGameService newGameService = new CreateGameService();
    CreateGameResult createGameResult = newGameService.createGame(new CreateGameRequest("Test Game", goodToken), dataService);

    @Test
    public void testBadRequest() {
        ListGamesService service = new ListGamesService();
        ListGamesResult result = service.listGames(new ListGamesRequest(badToken), dataService);
        assertEquals(401, result.responseCode());
        System.out.print(new Gson().toJson(result, ListGamesResult.class));
    }

    @Test
    public void testGoodRequest() {
        ListGamesService service = new ListGamesService();
        ListGamesResult result = service.listGames(new ListGamesRequest(goodToken), dataService);
        assertEquals(200, result.responseCode());
        System.out.print(new Gson().toJson(result, ListGamesResult.class));
    }

}