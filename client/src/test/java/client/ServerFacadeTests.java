package client;

import dataaccess.DataAccessDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseDAO;
import exceptions.ResponseException;
import org.junit.jupiter.api.*;
import returns.ListGamesReturn;
import server.Server;
import serverfacade.ServerFacade;
import requests.*;
import results.*;

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

        public CreateGameResult createGoodGame(String authToken, String gameName) throws ResponseException {
            return facade.createGame(new CreateGameRequest(gameName, authToken));
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

    @Test
    public void testCreateGamePositive() {
        try {
            RegisterResult res = requests.createUser1();
            CreateGameResult createResult1 = requests.createGoodGame(res.authToken(), "test game");
            CreateGameResult createResult2 = requests.createGoodGame(res.authToken(), "test game too");
            DataAccessResult daoResult = databaseDAO.gameData.isGameNumberValid(createResult1.gameID());
            Assertions.assertEquals("true", daoResult.data());

            daoResult = databaseDAO.gameData.isGameNumberValid(createResult2.gameID());
            Assertions.assertEquals("true", daoResult.data());

        } catch (DataAccessException | ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCreateGameNegative() {
        // This test is tricky, as the server does NOT throw an error if a game has a null name; it expects the
        // front end to do the validation
        try {
            RegisterResult res = requests.createUser1();
            CreateGameRequest createGameRequest = new CreateGameRequest("A cheesy match", "Cheese puffs");
            CreateGameResult result = facade.createGame(createGameRequest);
            DataAccessResult daoResult = databaseDAO.gameData.isGameNumberValid(result.gameID());

        } catch (DataAccessException | ResponseException e) {
            Assertions.assertTrue(true);
        }
    }

    @Test
    public void testListGamesPositive() throws ResponseException {
        RegisterResult res = requests.createUser1();
        requests.createGoodGame(res.authToken(), "test game");
        requests.createGoodGame(res.authToken(), "test game too");
        ListGamesRequest listGamesRequest = new ListGamesRequest(res.authToken());

        ListGamesReturn listGamesResult = facade.listGames(listGamesRequest);
        Assertions.assertEquals(2, listGamesResult.games().size());
    }

    @Test
    public void testListGamesNegative() throws ResponseException {
        try {
            RegisterResult res = requests.createUser1();
            requests.createGoodGame(res.authToken(), "test game");
            requests.createGoodGame(res.authToken(), "test game too");
            ListGamesRequest listGamesRequest = new ListGamesRequest("Peanut_Butter");

            ListGamesReturn listGamesResult = facade.listGames(listGamesRequest);
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }
    }

    @Test
    public void testJoinGamePositive() throws ResponseException, DataAccessException {
        RegisterResult res = requests.createUser1();
        CreateGameResult createGameResult =  requests.createGoodGame(res.authToken(), "test game");
        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", createGameResult.gameID(), res.authToken());
        JoinGameResult joinGameResult = facade.joinGame(joinGameRequest);
        DataAccessResult daoResult = databaseDAO.gameData.isColorAvailable(createGameResult.gameID(), "WHITE");
        Assertions.assertEquals("false", daoResult.data());
    }

    @Test
    public void testJoinGameNegative() throws ResponseException, DataAccessException {
        try {
            RegisterResult res = requests.createUser1();
            CreateGameResult createGameResult =  requests.createGoodGame(res.authToken(), "test game");
            JoinGameRequest joinGameRequest = new JoinGameRequest("Cheese", createGameResult.gameID(), res.authToken());
            JoinGameResult joinGameResult = facade.joinGame(joinGameRequest);
            DataAccessResult daoResult = databaseDAO.gameData.isColorAvailable(createGameResult.gameID(), "WHITE");
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }

    }

    @Test
    public void testJoinGameNegative2() throws ResponseException, DataAccessException {
        try {
            RegisterResult res = requests.createUser1();
            CreateGameResult createGameResult =  requests.createGoodGame(res.authToken(), "test game");
            JoinGameRequest joinGameRequest = new JoinGameRequest("Cheese", createGameResult.gameID(), "Cheese");
            JoinGameResult joinGameResult = facade.joinGame(joinGameRequest);
            DataAccessResult daoResult = databaseDAO.gameData.isColorAvailable(createGameResult.gameID(), "WHITE");
            Assertions.fail();
        } catch (Exception e) {
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