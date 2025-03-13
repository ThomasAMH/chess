package databasedao;

import com.google.gson.Gson;
import dataaccess.DataAccessDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseDAO;
import org.junit.jupiter.api.Test;
import requests.ListGamesRequest;
import requests.LoginRequest;
import results.ListGamesResult;
import service.CreateGameService;
import service.ListGamesService;
import service.LoginService;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GetGamesTest {
    DataAccessDAO dataService;
    String token;
    LoginService loginService = new LoginService();
    CreateGameService newGameService = new CreateGameService();
    public GetGamesTest() {
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
        ListGamesService service = new ListGamesService();
        ListGamesResult result = service.listGames(new ListGamesRequest(token), dataService);
        assertEquals(200, result.responseCode());
        System.out.print(new Gson().toJson(result, ListGamesResult.class));
    }

}