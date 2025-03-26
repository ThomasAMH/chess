package serverFacade;

import com.google.gson.Gson;
import exceptions.ResponseException;
import requests.*;
import results.*;
import returns.ListGamesReturn;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public RegisterResult addNewUser(RegisterRequest request) throws ResponseException {
        String path = "/user";
        return this.makeRequest("POST", path, request, RegisterResult.class);
    }

    public LoginResult loginUser(LoginRequest request) throws ResponseException {
        String path = "/session";
        return this.makeRequest("POST", path, request, LoginResult.class);
    }

    public LogoutResult logoutUser(LogoutRequest request) throws ResponseException {
        String path = "/session";
        return this.makeRequest("DELETE", path, request, LogoutResult.class);
    }

    public ListGamesReturn listGames(ListGamesRequest request) throws ResponseException {
        String path = "/game";
        return this.getGamesRequest(path, request, ListGamesReturn.class);
    }

    public CreateGameResult createGame(CreateGameRequest request) throws ResponseException {
        String path = "/game";
        return this.makeRequest("POST", path, request, CreateGameResult.class);
    }

    public JoinGameResult joinGame(JoinGameRequest request) throws ResponseException {
        String path = "/game";
        return this.makeRequest("PUT", path, request, JoinGameResult.class);
    }

    public void clearDatabase() throws ResponseException {
        String path = "/db";
        this.makeRequest("DELETE", path, null, null);
    }

    private <T> T getGamesRequest(String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            ListGamesRequest req = (ListGamesRequest) request;
            String authTokenString = "?authToken=" + req.authToken();
            URL url = new URI(serverUrl + path + authTokenString).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("GET");
            http.setDoOutput(true);
            http.setRequestProperty("authToken", req.authToken());

            //http.addRequestProperty("Content-Type", "application/json");
//            DataOutputStream out = new DataOutputStream(http.getOutputStream());
//            out.writeBytes(authTokenString);
//            out.flush();
//            out.close();

            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);

        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private <T> T makeRequest(String httpMethod, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = new URI(serverUrl + path).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(httpMethod);
            http.setDoOutput(true);

            if(request != null) {
                http.addRequestProperty("Content-Type", "application/json");
                String reqData = new Gson().toJson(request);
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
        if (status / 100 == 2) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(respErr);
                }
            } catch (ResponseException ex) {
                throw new ResponseException(status, "other failure: " + status);
            }
        }
    }
}
