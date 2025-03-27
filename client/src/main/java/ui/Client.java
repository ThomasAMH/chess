package ui;

import exceptions.ResponseException;
import requests.*;
import results.*;
import serverFacade.ServerFacade;

import java.util.Arrays;
import java.util.HashMap;

import static ui.EscapeSequences.*;

public class Client {
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.LOGGED_OUT;
    private static HashMap<String, String> activeAuthTokens = new HashMap<String, String>();
    private static String activeUser;

    public Client(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            cmd = cmd.toLowerCase();
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "logout" -> logout();
                case "create" -> createGame(params);
//                case "list" -> listGames();
//                case "play" -> playGame(params);
//                case "observe" -> observeGame();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String register(String... params) throws ResponseException {
        String exceptionString = SET_TEXT_COLOR_RED + "Expected: <USERNAME> <PASSWORD> <EMAIL>";
        if(params.length < 2) {
            throw new ResponseException(400, exceptionString);
        }
        if(params[0].isBlank() || params[1].isBlank() || params[2].isBlank()) {
            throw new ResponseException(400, exceptionString);
        }
        RegisterRequest request = new RegisterRequest(params[0], params[1], params[2]);
        RegisterResult result = server.addNewUser(request);

        state = State.LOGGED_IN;
        activeAuthTokens.put(result.username(), result.authToken());
        activeUser = result.username();
        return String.format(SET_TEXT_COLOR_BLUE + "\nYou signed in as %s.", activeUser);
    }

    public String login(String... params) throws ResponseException {
        String exceptionString = SET_TEXT_COLOR_RED + "Expected: <USERNAME> <PASSWORD>";
        if(params.length < 2) {
            throw new ResponseException(400, exceptionString);
        }
        if(params[0].isBlank() || params[1].isBlank()) {
            throw new ResponseException(400, exceptionString);
        }
        LoginRequest request = new LoginRequest(params[0], params[1]);
        LoginResult result = server.loginUser(request);

        state = State.LOGGED_IN;
        activeAuthTokens.put(result.username(), result.authToken());
        activeUser = result.username();
        return String.format(SET_TEXT_COLOR_BLUE + "\nYou signed in as %s.", activeUser);
    }

    public String logout() throws ResponseException {
        if(state == State.LOGGED_OUT) {
            throw new ResponseException(400, "You're not logged in");
        }
        LogoutRequest request = new LogoutRequest(activeAuthTokens.get(activeUser));
        LogoutResult result = server.logoutUser(request);

        state = State.LOGGED_OUT;
        activeAuthTokens.remove(activeUser);
        return String.format(SET_TEXT_COLOR_BLUE + "\nLogged out");
    }

    public String createGame(String... params) throws ResponseException {

        String exceptionString = SET_TEXT_COLOR_RED + "Expected: <GAMENAME>";
        if(params.length != 1) {
            throw new ResponseException(400, exceptionString);
        }
        if(params[0].isBlank()) {
            throw new ResponseException(400, exceptionString);
        }
        CreateGameRequest request = new CreateGameRequest(params[0], activeAuthTokens.get(activeUser));
        CreateGameResult result = server.createGame(request);

        return String.format(SET_TEXT_COLOR_BLUE + "\nGame created: %s.", params[0]);
    }
//
//    public String adoptPet(String... params) throws ResponseException {
//        assertSignedIn();
//        if (params.length == 1) {
//            try {
//                var id = Integer.parseInt(params[0]);
//                var pet = getPet(id);
//                if (pet != null) {
//                    server.deletePet(id);
//                    return String.format("%s says %s", pet.name(), pet.sound());
//                }
//            } catch (NumberFormatException ignored) {
//            }
//        }
//        throw new ResponseException(400, "Expected: <pet id>");
//    }
//
//    public String adoptAllPets() throws ResponseException {
//        assertSignedIn();
//        var buffer = new StringBuilder();
//        for (var pet : server.listPets()) {
//            buffer.append(String.format("%s says %s%n", pet.name(), pet.sound()));
//        }
//
//        server.deleteAllPets();
//        return buffer.toString();
//    }
//
//    public String logOut() throws ResponseException {
//        assertSignedIn();
//        ws.leavePetShop(visitorName);
//        ws = null;
//        state = State.SIGNEDOUT;
//        return String.format("%s left the shop", visitorName);
//    }
//
//    private Pet getPet(int id) throws ResponseException {
//        for (var pet : server.listPets()) {
//            if (pet.id() == id) {
//                return pet;
//            }
//        }
//        return null;
//    }

    public String help() {
        if (state == State.LOGGED_OUT) {
            return SET_TEXT_COLOR_YELLOW + """
                    - login <USERNAME> <PASSWORD>
                    - register <USERNAME> <PASSWORD> <EMAIL>
                    - quit
                    """;
        }
        return SET_TEXT_COLOR_YELLOW + """
                - logout
                - register <USERNAME> <PASSWORD> <EMAIL>
                - create <GAMENAME>
                - quit
                """;
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.LOGGED_OUT) {
            throw new ResponseException(400, "You must sign in");
        }
    }
}
