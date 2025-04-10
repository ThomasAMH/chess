package service;
import com.google.gson.Gson;
import dataaccess.DataAccessDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseDAO;
import dataaccess.MemoryDAO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import requests.LoginRequest;
import requests.RegisterRequest;
import results.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JoinGameTest {

    static DataAccessDAO dataService;
    static String authToken;
    RegisterRequest registerRequest = new RegisterRequest("mot", "12345", "mot@thebomb.com");
    LoginRequest goodLoginRequest = new LoginRequest("mot", "12345");
    LoginRequest badLoginRequest = new LoginRequest("mot", "54321");

    RegistrationService regService = new RegistrationService();
    RegisterResult result1 = regService.registerUser(registerRequest, dataService);

    String goodToken = result1.authToken();
    String badToken = "12345";

    CreateGameService newGameService = new CreateGameService();
    CreateGameResult createGameResult = newGameService.createGame(new CreateGameRequest("Test Game", goodToken),dataService);

    JoinGameService joinGameService = new JoinGameService();
    JoinGameRequest badJoinGameRequest = new JoinGameRequest("WHITE", 0, badToken);


    @BeforeAll
    public static void primeTests() {
        try {
            dataService = new DatabaseDAO();
            LoginRequest req = new LoginRequest("frank", "12345");
            LoginService service = new LoginService();
            authToken = service.loginUser(req, dataService).authToken();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testBadRequest() {
        JoinGameResult result = joinGameService.joinGame(badJoinGameRequest, dataService);
        assertEquals(401, result.responseCode());
        System.out.print(new Gson().toJson(result, JoinGameResult.class));
    }

    @Test
    public void testGoodRequest() {
        JoinGameRequest goodJoinGameRequest = new JoinGameRequest("BLACK", 275, authToken);
        JoinGameResult result = joinGameService.joinGame(goodJoinGameRequest, dataService);
        assertEquals(401, result.responseCode());
        System.out.print(new Gson().toJson(result, JoinGameResult.class));
    }

    @Test
    public void testJoinAgainRequest() {
        JoinGameRequest goodJoinGameRequest = new JoinGameRequest("WHITE", 273, authToken);
        JoinGameResult result = joinGameService.joinGame(goodJoinGameRequest, dataService);
        assertEquals(401, result.responseCode());
        System.out.print(new Gson().toJson(result, JoinGameResult.class));
    }

}