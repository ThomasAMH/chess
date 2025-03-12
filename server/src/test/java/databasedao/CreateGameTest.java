package databasedao;

import com.google.gson.Gson;
import com.mysql.cj.log.Log;
import dataaccess.DataAccessDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseDAO;
import dataaccess.MemoryDAO;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import requests.CreateGameRequest;
import requests.LoginRequest;
import requests.LogoutRequest;
import requests.RegisterRequest;
import results.CreateGameResult;
import results.RegisterResult;
import service.CreateGameService;
import service.LoginService;
import service.LogoutService;
import service.RegistrationService;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CreateGameTest {
    DataAccessDAO dataService;
    String token;
    LoginService loginService = new LoginService();
    CreateGameService newGameService = new CreateGameService();
    public CreateGameTest() {
        try {
            dataService = new DatabaseDAO();
//            RegistrationService regService = new RegistrationService();
//            RegisterRequest registerRequest = new RegisterRequest("Tom", "12345", "tom@thebomb.com");
            LoginRequest goodLoginRequest = new LoginRequest("Tom", "12345");
            token = loginService.loginUser(goodLoginRequest, dataService).authToken();
        } catch (DataAccessException e) {
            assertEquals(true, false);
        }
    }

    @Test
    public void testGoodRequest() {
        CreateGameResult createGameResult = newGameService.createGame(new CreateGameRequest("Test Game", token), dataService);
        assertEquals(200, createGameResult.responseCode());
        System.out.print(new Gson().toJson(createGameResult, CreateGameResult.class));
        LogoutService logoutService = new LogoutService();
        LogoutRequest logoutRequest = new LogoutRequest(token);
        logoutService.logoutUser(logoutRequest, dataService);
    }
}