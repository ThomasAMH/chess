package server;
import spark.Response;
import spark.Spark;
import spark.Request;

import java.util.HashMap;
import java.util.HashSet;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");
        RequestHandler handler = new RequestHandler();

        Spark.post("/user", handler::addNewUser);
        Spark.post("/session", handler::loginUser);
        Spark.delete("/session", handler::logoutUser);
        Spark.get("/game", handler::logoutUser);
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
        private HashSet<String> activeAuthTokens = new HashSet<String>();
        public Object addNewUser(Request req, Response res) {

        };
        public Object loginUser(Request req, Response res) {

        };
        public Object logoutUser(Request req, Response res) {
            String token = req.headers("authorization");
        };
        public Object getGame(Request req, Response res) {

        };
        public Object createGame(Request req, Response res) {

        };
        public Object joinGame(Request req, Response res) {

        };
        public Object nukeEverything(Request req, Response res) {

        };

        private boolean isUserAuthenticated(String token) {
            return activeAuthTokens.contains(token);
        }
    }
}