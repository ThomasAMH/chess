package databasedao;
import com.google.gson.Gson;
import dataaccess.DataAccessDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseDAO;
import dataaccess.MemoryDAO;
import org.junit.jupiter.api.Test;
import requests.JoinGameRequest;
import requests.LoginRequest;
import requests.LogoutRequest;
import requests.RegisterRequest;
import results.LogoutResult;
import results.RegisterResult;
import service.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NukeEverythingTest {
    DataAccessDAO dataService;
    String token;
    LoginService loginService = new LoginService();

    JoinGameRequest goodJoinGameRequest;

    public NukeEverythingTest() {
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
    public void testNuke() {
        dataService.nukeEverything();
    }

}