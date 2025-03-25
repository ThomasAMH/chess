package client;

import dataaccess.DataAccessDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseDAO;
import exceptions.ResponseException;
import org.junit.jupiter.api.*;
import requests.LoginRequest;
import requests.LogoutRequest;
import requests.RegisterRequest;
import results.DataAccessResult;
import results.LoginResult;
import results.LogoutResult;
import results.RegisterResult;
import server.Server;
import serverFacade.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private static Requests requests;
    private static DataAccessDAO databaseDAO;

    private static class Requests {
        public RegisterRequest goodRegisterRequest1 = new RegisterRequest("Tom", "12345", "tom@thebomb.com");
        public RegisterRequest goodRegisterRequest2 = new RegisterRequest("Jill", "666888", "jill@thebomb.com");
        public RegisterRequest badRegisterRequest = new RegisterRequest(null, "12345", "jerry@thebomb.com");

        public LoginRequest goodLoginRequest = new LoginRequest("Tom", "12345");
        public LoginRequest badLoginRequest = new LoginRequest("Tom", "11111");

        public RegisterResult createUser1() {
            try {
                return facade.addNewUser(goodRegisterRequest1);
            } catch (ResponseException e) {
                throw new RuntimeException(e);
            }
        }

        public LoginResult loginuser1() {
            try {
                return facade.loginUser(goodLoginRequest);
            } catch (ResponseException e) {
                throw new RuntimeException(e);
            }
        }

        public LogoutResult logoutUser1(String authToken) {
            try {
                LogoutRequest logoutRequest = new LogoutRequest(authToken);
                return facade.logoutUser(logoutRequest);
            } catch (ResponseException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @BeforeAll
    public static void init() {
        requests = new Requests();
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
        try {
            databaseDAO = new DatabaseDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    public void testRegistrationPositive() {
        try {
            RegisterResult result = requests.createUser1();
            DataAccessResult daoResult = databaseDAO.userData.doesUserExist(requests.goodRegisterRequest1.username());
            Assertions.assertEquals("true", daoResult.data());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testRegistrationNegative() {
        try {
            RegisterResult result = facade.addNewUser(requests.badRegisterRequest);
            DataAccessResult daoResult = databaseDAO.userData.doesUserExist(requests.badRegisterRequest.username());
            Assertions.fail();
        } catch (ResponseException | DataAccessException e) {
            Assertions.assertTrue(true);
        }
    }

    @Test
    public void testLogoutPositive() {
        try {
            RegisterResult result = requests.createUser1();
            String token = result.authToken();
            requests.logoutUser1(token);
            DataAccessResult daoResult = databaseDAO.authData.doesAuthTokenExist(token);
            Assertions.assertEquals("false", daoResult.data());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testLogoutNegative() {
        try {
            RegisterResult result = requests.createUser1();
            String token = result.authToken();
            LogoutResult logoutResult = requests.logoutUser1("cheese curds");
            Assertions.fail();
        } catch(Exception e) {
            Assertions.assertTrue(true);
        }

    }

    @Test
    public void testLoginPositive() {
        try {
            RegisterResult req = requests.createUser1();
            requests.logoutUser1(req.authToken());
            LoginResult loginResult = requests.loginuser1();
            DataAccessResult daoResult = databaseDAO.authData.doesAuthTokenExist(loginResult.authToken());
            Assertions.assertEquals("true", daoResult.data());

        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testLoginNegative() {
        try {
            RegisterResult req = requests.createUser1();
            requests.logoutUser1(req.authToken());
            LoginResult loginResult = facade.loginUser(requests.badLoginRequest);
            DataAccessResult daoResult = databaseDAO.authData.doesAuthTokenExist(loginResult.authToken());
            Assertions.assertEquals("false", daoResult.data());
            Assertions.fail();

        } catch (DataAccessException | ResponseException e) {
            Assertions.assertTrue(true);
        }
    }

    @AfterEach
    void clearDatabase() throws ResponseException {
        facade.clearDatabase();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

}