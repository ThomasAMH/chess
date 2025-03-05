import com.google.gson.Gson;
import dataaccess.DataAccessDAO;
import dataaccess.MemoryDAO;
import org.junit.jupiter.api.Test;
import requests.CreateGameRequest;
import requests.LoginRequest;
import requests.RegisterRequest;
import results.CreateGameResult;
import results.RegisterResult;
import service.CreateGameService;
import service.RegistrationService;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CreateGameTest {
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

    @Test
    public void testBadRequest() {
        CreateGameResult createGameResult = newGameService.createGame(new CreateGameRequest("Test Game", badToken), dataService);
        assertEquals(401, createGameResult.responseCode());
        System.out.print(new Gson().toJson(createGameResult, CreateGameResult.class));
    }

    @Test
    public void testGoodRequest() {
        CreateGameResult createGameResult = newGameService.createGame(new CreateGameRequest("Test Game", goodToken), dataService);
        assertEquals(200, createGameResult.responseCode());
        System.out.print(new Gson().toJson(createGameResult, CreateGameResult.class));
    }

}