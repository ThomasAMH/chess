package service;
import com.google.gson.Gson;
import dataaccess.DataAccessDAO;
import dataaccess.MemoryDAO;
import org.junit.jupiter.api.Test;
import requests.LogoutRequest;
import requests.RegisterRequest;
import results.LogoutResult;
import results.RegisterResult;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NukeEverythingTest {
    DataAccessDAO dataService = new MemoryDAO();
    RegisterRequest registerRequest = new RegisterRequest("Tom", "12345", "tom@thebomb.com");

    LogoutRequest badLogoutRequest = new LogoutRequest("12345");
    RegistrationService service;

    RegistrationService regService = new RegistrationService();
    RegisterResult result1 = regService.registerUser(registerRequest, dataService);
    LogoutRequest goodLogoutRequest = new LogoutRequest(result1.authToken());

    @Test
    public void testNuke() {
        dataService.nukeEverything();
        LogoutService logoutService = new LogoutService();
        LogoutResult result = logoutService.logoutUser(badLogoutRequest, dataService);

        assertEquals(401, result.responseCode());
        System.out.print(new Gson().toJson(result, LogoutResult.class));
    }

}