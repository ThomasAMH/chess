package databasedao;
import com.google.gson.Gson;
import dataaccess.DataAccessDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseDAO;
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
import service.LoginService;
import service.RegistrationService;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JoinGameTest {
    DataAccessDAO dataService;
    String token;
    LoginService loginService = new LoginService();
    CreateGameService newGameService = new CreateGameService();
    JoinGameService joinGameService = new JoinGameService();
    JoinGameRequest goodJoinGameRequest;

    public JoinGameTest() {
        try {
            dataService = new DatabaseDAO();
            LoginRequest goodLoginRequest = new LoginRequest("Tom", "12345");
            token = loginService.loginUser(goodLoginRequest, dataService).authToken();
            goodJoinGameRequest = new JoinGameRequest("WHITE", 3, "Tom", token);
        } catch (DataAccessException e) {
            assertEquals(true, false);
        }
    }

    @Test
    public void testGoodRequest() {
        JoinGameResult result = joinGameService.joinGame(goodJoinGameRequest, dataService);
        assertEquals(200, result.responseCode());
        System.out.print(new Gson().toJson(result, JoinGameResult.class));
    }

}