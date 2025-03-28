package serverFacade;

import com.google.gson.Gson;
import exceptions.ResponseException;
import returns.ListGamesReturn;
import requests.*;
import results.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public RegisterResult addNewUser(RegisterRequest request) throws ResponseException {
        String path = "/user";
        return this.makeRequest("POST", path, request, RegisterResult.class, null);
    }

    public LoginResult loginUser(LoginRequest request) throws ResponseException {
        String path = "/session";
        return this.makeRequest("POST", path, request, LoginResult.class, null);
    }

    public LogoutResult logoutUser(LogoutRequest request) throws ResponseException {
        String path = "/session";
        return this.makeRequest("DELETE", path, request, LogoutResult.class, request.authToken());
    }

    public ListGamesReturn listGames(ListGamesRequest request) throws ResponseException {
        String path = "/game";
        return this.getGamesRequest(path, request, ListGamesReturn.class);
    }

    public CreateGameResult createGame(CreateGameRequest request) throws ResponseException {
        String path = "/game";
        return this.makeRequest("POST", path, request, CreateGameResult.class, request.authToken());
    }

    public JoinGameResult joinGame(JoinGameRequest request) throws ResponseException {
        String path = "/game";
        return this.makeRequest("PUT", path, request, JoinGameResult.class, request.authToken());
    }

    public void clearDatabase() throws ResponseException {
        String path = "/db";
        this.makeRequest("DELETE", path, null, null, null);
    }

    private <T> T getGamesRequest(String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            ListGamesRequest req = (ListGamesRequest) request;
            String authTokenString = "?authorization=" + req.authToken();
            URL url = new URI(serverUrl + path + authTokenString).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("GET");
            http.setDoOutput(true);
            http.setRequestProperty("authorization", req.authToken());

            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);

        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private <T> T makeRequest(String httpMethod, String path, Object request, Class<T> responseClass, String authToken) throws ResponseException {
        try {
            URL url = new URI(serverUrl + path).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(httpMethod);
            http.setDoOutput(true);

            if(request != null) {
                http.addRequestProperty("Content-Type", "application/json");
                String reqData = new Gson().toJson(request);
                http.setRequestProperty("authorization", authToken);
                try (OutputStream reqBody = http.getOutputStream()) {
                    reqBody.write(reqData.getBytes());
                }
            }
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);

        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (status / 100 != 2) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(respErr);
                }
            } catch (ResponseException ex) {
                throw new ResponseException(status, ex.getMessage());
            }
        }
    }
}
