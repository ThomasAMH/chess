package server;
import com.google.gson.Gson;
import dataaccess.MemoryDAO;
import model.GameData;
import model.GameMetaData;
import requests.*;
import results.*;
import returns.*;
import service.*;
import spark.Response;
import spark.Spark;
import spark.Request;
import dataaccess.DataAccessDAO;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;

public class Server {
    //Change this to DB later implementation
    static DataAccessDAO dataService = new MemoryDAO();
    public int run(int desiredPort) {

        Spark.port(desiredPort);
        Spark.staticFiles.location("web");
        RequestHandler handler = new RequestHandler();

        Spark.post("/user", handler::addNewUser);
        Spark.post("/session", handler::loginUser);
        Spark.delete("/session", handler::logoutUser);
        Spark.get("/game", handler::getGame);
        Spark.post("/game", handler::createGame);
        Spark.put("/game", handler::joinGame);
        Spark.delete("/db", handler::nukeEverything);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
    }

    public static class RequestHandler {
        private static HashSet<String> activeAuthTokens = new HashSet<String>();

        public Object addNewUser(Request req, Response res) {
            //TODO: Add some error checking here
            RegisterRequest requestData = new Gson().fromJson(req.body(), RegisterRequest.class);
            if(requestData.username().isEmpty() || requestData.email().isEmpty() || requestData.password().isEmpty()) {
                res.status(400);
                return formatErrorString("Error: bad request");
            }
            RegistrationService service = new RegistrationService();
            RegisterResult result = service.registerUser(requestData, dataService);

            if(result.responseCode() == 200) {
                RegisterReturn returnVal = new RegisterReturn(result.username(), result.authToken());
                activeAuthTokens.add(result.authToken());
                res.status(200);
                return new Gson().toJson(returnVal);
            }
            else {
                res.status(result.responseCode());
                return formatErrorString(result.responseMessage());
            }
        };
        public Object loginUser(Request req, Response res) {
            LoginRequest requestData = new Gson().fromJson(req.body(), LoginRequest.class);
            if(requestData.username().isEmpty() || requestData.password().isEmpty()) {
                res.status(400);
                return formatErrorString("Error: bad request");
            }

            LoginService service = new LoginService();
            LoginResult result = service.loginUser(requestData, dataService);

            if(result.responseCode() == 200) {
                LoginReturn returnVal = new LoginReturn(result.username(), result.authToken());
                activeAuthTokens.add(result.authToken());
                res.status(200);
                return new Gson().toJson(returnVal);
            }
            else {
                res.status(result.responseCode());
                return formatErrorString(result.responseMessage());
            }
        };
        public Object logoutUser(Request req, Response res) {
            LogoutRequest requestData = new Gson().fromJson(req.body(), LogoutRequest.class);
            if(requestData.authToken().isEmpty()) {
                res.status(500);
                return formatErrorString("Error: no auth token provided");
            }

            LogoutService service = new LogoutService();
            LogoutResult result = service.logoutUser(requestData, dataService);

            if(result.responseCode() == 200) {
                activeAuthTokens.remove(requestData.authToken());
                res.status(200);
                return "";
            }
            else {
                res.status(result.responseCode());
                return formatErrorString(result.responseMessage());
            }
        };
        public Object getGame(Request req, Response res) {
            ListGamesRequest requestData = new Gson().fromJson(req.body(), ListGamesRequest.class);
            if(requestData.authToken().isEmpty()) {
                res.status(500);
                return formatErrorString("Error: no auth token provided");
            }

            ListGamesService service = new ListGamesService();
            ListGamesResult result = service.listGames(new ListGamesRequest(requestData.authToken()), dataService);

            if(result.responseCode() == 200) {
                ArrayList<GameMetaData> returnList = new ArrayList<GameMetaData>();
                GameMetaData gameData;
                for(GameData data: result.games()) {
                    gameData = new GameMetaData(data.gameID(), data.whiteUsername(), data.blackUsername(), data.gameName());
                    returnList.add(gameData);
                }
                ListGamesReturn returnVal = new ListGamesReturn(gameData);
                res.status(200);
                return new Gson().toJson(returnVal);
            }
            else {
                res.status(result.responseCode());
                return formatErrorString(result.responseMessage());
            }
        };
        public Object createGame(Request req, Response res) {
            CreateGameRequest requestData = new Gson().fromJson(req.body(), CreateGameRequest.class);
            if(requestData.authToken().isEmpty()) {
                res.status(400);
                return formatErrorString("Error: no auth token provided");
            }
            CreateGameService service = new CreateGameService();
            CreateGameResult result = service.createGame(requestData, dataService);

            if(result.responseCode() == 200) {
                res.status(200);
                CreateGameReturn returnVal = new CreateGameReturn(result.gameName());
                return new Gson().toJson(returnVal);
            }
            else {
                res.status(result.responseCode());
                return formatErrorString(result.responseMessage());
            }
        };
        public Object joinGame(Request req, Response res) {
            JoinGameRequest requestData = new Gson().fromJson(req.body(), JoinGameRequest.class);
            if(req.headers("authorization").isEmpty()) {
                res.status(400);
                return formatErrorString("Error: no auth token provided");
            }
            JoinGameService service = new JoinGameService();
            JoinGameResult result = service.joinGame(requestData, dataService);

            if(result.responseCode() == 200) {
                JoinGameReturn returnVal = new JoinGameReturn(requestData.playerColor(), requestData.gameID());
                res.status(200);
                return new Gson().toJson(returnVal);
            }
            else {
                res.status(result.responseCode());
                return formatErrorString(result.responseMessage());
            }
        };
        public Object nukeEverything(Request req, Response res) {
            dataService.nukeEverything();
            res.status(200);
            return "Nuking complete.";
        };
        private static String formatErrorString(String errorMessage) {
            return "{\"message\": \"" + errorMessage + "\"}";
        }

        private boolean isUserAuthenticated(String token) {
            return activeAuthTokens.contains(token);
        }
    }
}