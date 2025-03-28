package server;
import bodyobjects.CreateGameBodyObj;
import bodyobjects.JoinGameBodyObj;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseDAO;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Server {
    static DataAccessDAO dataService;

    public Server() {
        try {
            dataService = new DatabaseDAO();
        } catch (DataAccessException e) {
            dataService = new MemoryDAO();
        }
    }

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
        Spark.delete("/db", handler::clearDatabase);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
    }

    public static class RequestHandler {

        public Object addNewUser(Request req, Response res) {
            RegisterRequest requestData = new Gson().fromJson(req.body(), RegisterRequest.class);
            if(requestData.username() == null || requestData.email() == null || requestData.password() == null) {
                res.status(400);
                return formatErrorString("Error: bad request", res.status());
            }
            RegistrationService service = new RegistrationService();
            RegisterResult result = service.registerUser(requestData, dataService);

            if(result.responseCode() == 200) {
                RegisterReturn returnVal = new RegisterReturn(result.username(), result.authToken());
                res.status(200);
                return new Gson().toJson(returnVal);
            }
            else {
                res.status(result.responseCode());
                return formatErrorString(result.responseMessage(), res.status());
            }
        }
        public Object loginUser(Request req, Response res) {
            LoginRequest requestData = new Gson().fromJson(req.body(), LoginRequest.class);
            if(requestData.username().isEmpty() || requestData.password().isEmpty()) {
                res.status(400);
                return formatErrorString("Error: bad request", res.status());
            }

            LoginService service = new LoginService();
            LoginResult result = service.loginUser(requestData, dataService);

            if(result.responseCode() == 200) {
                LoginReturn returnVal = new LoginReturn(result.username(), result.authToken());
                res.status(200);
                return new Gson().toJson(returnVal);
            }
            else {
                res.status(result.responseCode());
                return formatErrorString(result.responseMessage(), res.status());
            }
        }
        public Object logoutUser(Request req, Response res) {
            LogoutRequest requestData = new Gson().fromJson(req.body(), LogoutRequest.class);
            if(requestData.authToken().isEmpty()) {
                res.status(500);
                return formatErrorString("Error: no auth token provided", res.status());
            }

            LogoutService service = new LogoutService();
            LogoutResult result = service.logoutUser(requestData, dataService);

            if(result.responseCode() == 200) {
                res.status(200);
                return "";
            }
            else {
                res.status(result.responseCode());
                return formatErrorString(result.responseMessage(), res.status());
            }
        }
        public Object getGame(Request req, Response res) {
            ListGamesRequest requestData = new ListGamesRequest(req.headers("authToken"));
            if(requestData.authToken().isEmpty()) {
                res.status(500);
                return formatErrorString("Error: no auth token provided", res.status());
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
                ListGamesReturn returnVal = new ListGamesReturn(200, returnList);
                res.status(200);
                return new Gson().toJson(returnVal);
            }
            else {
                res.status(result.responseCode());
                return formatErrorString(result.responseMessage(), res.status());
            }
        }
        public Object createGame(Request req, Response res) {
            CreateGameRequest requestData =  new Gson().fromJson(req.body(), CreateGameRequest.class);
            CreateGameBodyObj bodyObj;
            bodyObj = new CreateGameBodyObj(requestData.gameName());
            if(requestData.authToken().isEmpty()) {
                res.status(400);

                return formatErrorString("Error: no auth token provided", res.status());
            }
            CreateGameService service = new CreateGameService();
            CreateGameResult result = service.createGame(requestData, dataService);

            if(result.responseCode() == 200) {
                res.status(200);
                CreateGameReturn returnVal = new CreateGameReturn(result.gameID());
                return new Gson().toJson(returnVal);
            }
            else {
                res.status(result.responseCode());
                return formatErrorString(result.responseMessage(), res.status());
            }
        }
        public Object joinGame(Request req, Response res) {
            JoinGameRequest request = new Gson().fromJson(req.body(), JoinGameRequest.class);
            JoinGameBodyObj bodyObj =  new JoinGameBodyObj(request.playerColor(), request.gameID());
            String activeToken = request.authToken();
            if(activeToken.isEmpty() ||
                    (!Objects.equals(bodyObj.playerColor(), "WHITE") &&
                            !Objects.equals(bodyObj.playerColor(), "BLACK"))) {
                res.status(400);
                return formatErrorString("Error: Bad request format; check auth token or colors", res.status());
            }
            JoinGameService service = new JoinGameService();
            JoinGameResult result = service.joinGame(request, dataService);

            if(result.responseCode() == 200) {
                JoinGameReturn returnVal = new JoinGameReturn(result.responseCode(),request.playerColor(), request.gameID());
                res.status(200);
                return new Gson().toJson(returnVal);
            }
            else {
                res.status(result.responseCode());
                return formatErrorString(result.responseMessage(), res.status());
            }
        }
        public Object clearDatabase(Request req, Response res) {
            dataService.clearDatabase();
            res.status(200);
            return "{}";
        }
        private static String formatErrorString(String errorMessage, int status) {
            HashMap<String, String> returnVal = new HashMap<String, String>();
            returnVal.put("status", String.valueOf(status));
            returnVal.put("message", errorMessage);
            return new Gson().toJson(returnVal);
        }

    }
}