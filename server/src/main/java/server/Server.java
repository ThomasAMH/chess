package server;
import com.google.gson.Gson;
import dataaccess.MemoryDAO;
import requests.ListGamesRequest;
import requests.LoginRequest;
import requests.LogoutRequest;
import requests.RegisterRequest;
import results.ListGamesResult;
import results.LoginResult;
import results.LogoutResult;
import results.RegisterResult;
import service.ListGamesService;
import service.LoginService;
import service.LogoutService;
import service.RegistrationService;
import spark.Response;
import spark.Spark;
import spark.Request;
import dataaccess.DataAccessDAO;

import java.util.HashSet;

public class Server {
    //Change this to DB later implementation
    static DataAccessDAO dataService = new MemoryDAO();
    public int run(int desiredPort) {

        Spark.port(desiredPort);
        Spark.staticFiles.location("web");
        RequestHandler handler = new RequestHandler();

        Spark.post("/user", handler::addNewUser);
//        Spark.post("/session", handler::loginUser);
//        Spark.delete("/session", handler::logoutUser);
//        Spark.get("/game", handler::logoutUser);
//        Spark.post("/game", handler::createGame);
//        Spark.put("/game", handler::joinGame);
//        Spark.delete("/db", handler::nukeEverything);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
    }

    public static class RequestHandler {
        private HashSet<String> activeAuthTokens = new HashSet<String>();

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
                activeAuthTokens.add(result.authToken());
                res.status(200);
                return new Gson().toJson(result);
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
                activeAuthTokens.add(result.authToken());
                res.status(200);
                return new Gson().toJson(result);
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
                return new Gson().toJson(result);
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
                activeAuthTokens.remove(requestData.authToken());
                res.status(200);
                return new Gson().toJson(result);
            }
            else {
                res.status(result.responseCode());
                return formatErrorString(result.responseMessage());
            }
        };
//        public Object createGame(Request req, Response res) {
//
//        };
//        public Object joinGame(Request req, Response res) {
//
//        };
//        public Object nukeEverything(Request req, Response res) {
//
//        };
        private String formatErrorString(String errorMessage) {
            return "{\"message\": \"" + errorMessage + "\"}";
        }

        private boolean isUserAuthenticated(String token) {
            return activeAuthTokens.contains(token);
        }
    }
}