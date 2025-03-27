package ui;

import exceptions.ResponseException;
import requests.*;
import results.*;
import serverFacade.ServerFacade;

import java.util.Arrays;

import static ui.EscapeSequences.*;

public class Client {
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.LOGGED_OUT;

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
//                case "login" -> login(params);
//                case "logout" -> logOut();
//                case "create" -> createGame(params);
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
        return String.format(SET_TEXT_COLOR_BLUE + "\nYou signed in as %s.", params[0]);
    }

//    public String login(String... params) throws ResponseException {
//        if (params.length >= 1) {
//            state = State.SIGNEDIN;
//            visitorName = String.join("-", params);
//            ws = new WebSocketFacade(serverUrl, notificationHandler);
//            ws.enterPetShop(visitorName);
//            return String.format("You signed in as %s.", visitorName);
//        }
//        throw new ResponseException(400, "Expected: <yourname>");
//    }
//
//    public String rescuePet(String... params) throws ResponseException {
//        assertSignedIn();
//        if (params.length >= 2) {
//            var name = params[0];
//            var type = PetType.valueOf(params[1].toUpperCase());
//            var pet = new Pet(0, name, type);
//            pet = server.addPet(pet);
//            return String.format("You rescued %s. Assigned ID: %d", pet.name(), pet.id());
//        }
//        throw new ResponseException(400, "Expected: <name> <CAT|DOG|FROG>");
//    }
//
//    public String listPets() throws ResponseException {
//        assertSignedIn();
//        var pets = server.listPets();
//        var result = new StringBuilder();
//        var gson = new Gson();
//        for (var pet : pets) {
//            result.append(gson.toJson(pet)).append('\n');
//        }
//        return result.toString();
//    }
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
                - login <USERNAME> <PASSWORD>
                - register <USERNAME> <PASSWORD> <EMAIL>
                - quit
                """;
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.LOGGED_OUT) {
            throw new ResponseException(400, "You must sign in");
        }
    }
}
