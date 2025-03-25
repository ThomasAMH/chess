package client;

import dataaccess.DataAccessDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseDAO;
import exceptions.ResponseException;
import org.junit.jupiter.api.*;
import requests.CreateGameRequest;
import requests.LoginRequest;
import requests.RegisterRequest;
import results.DataAccessResult;
import results.RegisterResult;
import server.Server;
import serverFacade.ServerFacade;

import javax.xml.crypto.Data;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private static Requests requests;
    private static DataAccessDAO databaseDAO;

    private static class Requests {
        public RegisterRequest goodRegisterRequest1 = new RegisterRequest("Tom", "12345", "tom@thebomb.com");
        public RegisterRequest goodRegisterRequest2 = new RegisterRequest("Jill", "666888", "jill@thebomb.com");
        public RegisterRequest badRegisterRequest = new RegisterRequest(null, null, null);

        public LoginRequest goodLoginRequest = new LoginRequest("Tom", "12345");

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
            RegisterResult result = facade.addNewUser(requests.goodRegisterRequest1);
            DataAccessResult daoResult = databaseDAO.userData.doesUserExist(requests.goodRegisterRequest1.username());
            Assertions.assertEquals("true", daoResult.data());
        } catch (ResponseException | DataAccessException e) {
            throw new RuntimeException(e);
        }

    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }
    static void clearDatabase() throws ResponseException {
        facade.clearDatabase();
    }

}