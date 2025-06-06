package service;
import com.google.gson.Gson;
import dataaccess.DataAccessDAO;
import dataaccess.MemoryDAO;
import org.junit.jupiter.api.Test;
import requests.LoginRequest;
import requests.RegisterRequest;
import results.LoginResult;
import results.RegisterResult;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoginServiceTest {
    DataAccessDAO dataService = new MemoryDAO();
    RegisterRequest registerRequest = new RegisterRequest("Tom", "12345", "tom@thebomb.com");
    LoginRequest goodLoginRequest = new LoginRequest("Tom", "12345");
    LoginRequest badLoginRequest = new LoginRequest("Tom", "54321");
    RegistrationService service;

    RegistrationService regService = new RegistrationService();
    RegisterResult result1 = regService.registerUser(registerRequest, dataService);

    @Test
    public void testGoodLogin() {
        LoginService loginService = new LoginService();
        LoginResult loginResult = loginService.loginUser(goodLoginRequest, dataService);

        assertEquals(200, loginResult.responseCode());
        assertEquals("Tom", loginResult.username());
        System.out.print(new Gson().toJson(loginResult, LoginResult.class));
    }

    @Test
    public void testBadLogin() {
        LoginService loginService = new LoginService();
        LoginResult loginResult = loginService.loginUser(badLoginRequest, dataService);

        assertEquals(401, loginResult.responseCode());
        assertEquals("Tom", loginResult.username());
        System.out.print(new Gson().toJson(loginResult, LoginResult.class));
    }

}