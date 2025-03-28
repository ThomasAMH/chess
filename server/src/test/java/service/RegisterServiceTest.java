package service;
import com.google.gson.Gson;
import dataaccess.DataAccessDAO;
import dataaccess.MemoryDAO;
import org.junit.jupiter.api.Test;
import requests.RegisterRequest;
import results.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterServiceTest {
    DataAccessDAO dataService = new MemoryDAO();
    RegisterRequest request1 = new RegisterRequest("Tom", "12345", "tom@thebomb.com");
    RegisterRequest request2 = new RegisterRequest("Tom", "12345", "tom@thebomb.com");
    RegistrationService service;

    @Test
    public void testAddition() {
        RegistrationService service = new RegistrationService();
        RegisterResult result1 = service.registerUser(request1, dataService);
        assertEquals(200, result1.responseCode());
        assertEquals("Tom", result1.username());
        System.out.print(new Gson().toJson(result1, RegisterResult.class));

    }

    @Test
    public void attemptDuplicateAddition() {
        RegistrationService service = new RegistrationService();
        RegisterResult result1 = service.registerUser(request1, dataService);
        assertEquals(200, result1.responseCode());
        assertEquals("Tom", result1.username());


        RegisterResult result2 = service.registerUser(request2, dataService);
        assertEquals(403, result2.responseCode());
        System.out.print(new Gson().toJson(result2, RegisterResult.class));
    }
}
