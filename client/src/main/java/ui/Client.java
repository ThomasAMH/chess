package ui;

import chess.*;
import exceptions.ResponseException;
import model.GameMetaData;
import returns.ListGamesReturn;
import serverfacade.ServerFacade;

import java.util.*;

import requests.*;
import results.*;


import static ui.EscapeSequences.*;

public class Client {
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.LOGGED_OUT;
    private static HashMap<String, String> activeAuthTokens = new HashMap<String, String>();
    private static String activeUser;
    private static HashMap<Integer, Integer> gameList;
    private String whiteUsername, blackusername;
    private ChessGame.TeamColor playerColor;
    private ChessGame activeGame;

    public Client(String serverUrl) {
        playerColor = ChessGame.TeamColor.WHITE;
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        activeGame = new ChessGame();
    }

    // For testing
    public Client(String serverUrl, ChessGame game) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.activeGame = game;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            cmd = cmd.toLowerCase();
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (state) {
                case State.LOGGED_OUT -> handleLoggedOutRequests(cmd, params);
                case State.LOGGED_IN -> handleLoggedInRequests(cmd, params);
                case State.PLAYING -> handlePlayingRequests(cmd, params);
                case State.OBSERVING -> handleObservingRequests(cmd, params);
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private String handleLoggedOutRequests(String cmd, String[] params) throws ResponseException {
        return switch (cmd) {
            case "register" -> register(params);
            case "login" -> login(params);
            case "quit" -> "quit";
            default -> help();
        };
    }

    private String handleLoggedInRequests(String cmd, String[] params) throws ResponseException {
        return switch (cmd) {
            case "logout" -> logout();
            case "create" -> createGame(params);
            case "list" -> listGames();
            case "play" -> playGame(params);
            case "observe" -> observeGame(params);
            case "quit" -> "quit";
            default -> help();
        };
    }

    private String handlePlayingRequests(String cmd, String[] params) throws ResponseException {
        return switch (cmd) {
            case "moves" -> showMoves(params);
            case "leave" -> leaveGameMode();
            case "move" -> makeMove(params);
            case "redraw" -> redrawGameScreen();
            case "resign" -> resignFromGame();
            default -> help();
        };
    }
    private String handleObservingRequests(String cmd, String[] params) throws ResponseException {
        return switch (cmd) {
            case "redraw" -> redrawGameScreen();
            case "leave" -> leaveGameMode();
            default -> help();
        };
    }

    private String makeMove(String... params) throws ResponseException {
        if(activeGame.getTeamTurn() != playerColor) {
            String exceptionString = SET_TEXT_COLOR_RED + "It's not your turn!";
            throw new ResponseException(400, exceptionString);
        }

        String exceptionString = SET_TEXT_COLOR_RED + """
                r1,c1 r2,c2
                - Where r1 and c1 are the row and column of the moving piece,
                  and r2 c2 are the row and column where you'd like to move the piece""";
        if(params.length != 2) {
            throw new ResponseException(400, exceptionString);
        }

        String[] coord1;
        String[] coord2 = params[1].split(",");
        coord1 = params[0].split(",");
        try {
            validateUserCoord(coord1);
            validateUserCoord(coord2);
        } catch (ResponseException e) {
            throw new ResponseException(400, e.getMessage());
        }
        int row1 = Integer.parseInt(coord1[1]);
        String col1 = "";
        col1 = coord1[0].toLowerCase();
        char col1Char = col1.charAt(0);
        int col1Int = -1;
        for(int i = 0; i < 8; i++) {
            if('a' + i == col1Char) {
                col1Int = i + 1;
                break;
            }
        }

        int row2 = Integer.parseInt(coord2[1]);
        String col2 = coord2[0].toLowerCase();
        char col2Char = col2.charAt(0);
        int col2Int = -1;
        for(int i = 0; i < 8; i++) {
            if('a' + i == col2Char) {
                col2Int = i + 1;
                break;
            }
        }
        if(col1Int == -1 || col2Int == -1) {
            exceptionString = SET_TEXT_COLOR_RED + "Expected: c,r \n where r is a number (1-8) and c is a letter (a-h)";
            throw new ResponseException(400, exceptionString);
        }

        ChessPosition startPos = new ChessPosition(row1, col1Int);
        ChessPosition endPos = new ChessPosition(row2, col2Int);
        ChessMove move = new ChessMove(startPos, endPos, null);
        try {
            activeGame.makeMove(move);
        } catch(chess.InvalidMoveException e) {
            exceptionString = SET_TEXT_COLOR_RED + "Error: Illegal move";
            throw new ResponseException(400, exceptionString);
        }
        redrawGameScreen();
        return "Move executed successfully";
    }

    private String resignFromGame() {
        state = State.OBSERVING;
        return "You have resigned. Enter leave to return to the home menu";
    }

    private String redrawGameScreen() {
        System.out.print(ERASE_SCREEN);
        printGameBoard();
        return "";
    }

    private String leaveGameMode() {
        System.out.print(ERASE_SCREEN);
        state = State.LOGGED_IN;
        return "Returned to home menu";
    }

    private String showMoves(String... params) throws ResponseException {
        String exceptionString = SET_TEXT_COLOR_RED + "Expected: c,r";
        if(params.length != 1) {
            throw new ResponseException(400, exceptionString);
        }
        String[] args;
        args = params[0].split(",");
        if(args.length < 2) {
            throw new ResponseException(400, exceptionString);
        }
        try {
            validateUserCoord(args);
        } catch (ResponseException e) {
            throw new ResponseException(400, e.getMessage());
        }
        int row = Integer.parseInt(args[1]);
        String col;
        col = args[0].toLowerCase();
        char colChar = col.charAt(0);
        int colInt = -1;
        for(int i = 0; i < 8; i++) {
            if('a' + i == colChar) {
                colInt = i + 1;
                break;
            }
        }
        if(colInt == -1) {
            exceptionString = SET_TEXT_COLOR_RED + "Expected: c,r \n where r is a number (1-8) and c is a letter (a-h)";
            throw new ResponseException(400, exceptionString);
        }

        BoardDrawer bd = new BoardDrawer(activeGame.getBoard());
        boolean hasMoves = bd.showPieceMoves(row, colInt, activeGame.getTeamTurn());

        if(hasMoves) {
            return "Showing moves for piece at " + args[0] + "," + args[1];
        }
        return "No moves to show for the piece at " + args[0] + "," + args[1];
    }

    public String register(String... params) throws ResponseException {
        String exceptionString = SET_TEXT_COLOR_RED + "Expected: <USERNAME> <PASSWORD> <EMAIL>";
        if(params.length < 3) {
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
        assertSignedIn();
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

    public String listGames() throws ResponseException {
        assertSignedIn();
        ListGamesRequest request = new ListGamesRequest(activeAuthTokens.get(activeUser));
        ListGamesReturn result = server.listGames(request);
        ArrayList<GameMetaData> games = result.games();
        gameList = new HashMap<Integer, Integer>();

        int i = 1;
        String gameDataString;
        String whitePlayer;
        String blackPlayer;
        System.out.println("Running Games:");
        for(GameMetaData game: games) {
            whitePlayer = (game.whiteUsername() == null ) ? "None" : game.whiteUsername();
            blackPlayer = (game.blackUsername() == null) ? "None" : game.blackUsername();
            gameDataString = "Game Number: " + String.valueOf(i) + ", Game Name: "+ game.gameName() +
                    ", White Player: " + whitePlayer + ", Black Player: " + blackPlayer;
            gameList.put(i, game.gameID());
            i++;
            System.out.println(SET_TEXT_COLOR_MAGENTA + "\t" + gameDataString);
        }

        return SET_TEXT_COLOR_BLUE + "Join a game by running the play command and providing the game number and color";
    }

    public String playGame(String... params) throws ResponseException {
        assertSignedIn();
        String exceptionString = SET_TEXT_COLOR_RED + "Expected: <GAMEID> <WHITE/BLACK>";
        Integer trueGameIndex;
        if(params.length < 2) {
            throw new ResponseException(400, exceptionString);
        }
        else if(params[0].isBlank() || params[1].isBlank()) {
            throw new ResponseException(400, exceptionString);
        }
        trueGameIndex = validateGameNumber(exceptionString, params);

        String color = params[1].toLowerCase();
        if(!color.equals("white") && !color.equals("black")) {
            throw new ResponseException(400, exceptionString + SET_TEXT_ITALIC + " must provide color: WHITE/BLACK");
        }

        JoinGameRequest request = new JoinGameRequest(color.toUpperCase(), trueGameIndex, activeAuthTokens.get(activeUser));
        JoinGameResult result = server.joinGame(request);

        if(color.equals("white")) {
            playerColor = ChessGame.TeamColor.WHITE;
        } else {
            playerColor = ChessGame.TeamColor.BLACK;
        }
        printGameBoard();
        state = State.PLAYING;

        return SET_TEXT_COLOR_BLUE + "Game joined successfully. Good luck!";
    }

    private void printGameBoard() {
        BoardDrawer bd = new BoardDrawer(activeGame.getBoard());
        bd.drawChessBoard(whiteUsername, blackusername, activeGame.getTeamTurn(), playerColor, activeGame.getBoard());
    }

    public String observeGame(String... params) throws ResponseException {
        assertSignedIn();
        String exceptionString = SET_TEXT_COLOR_RED + "Expected: <GAMEID>";
        if(params.length < 1) {
            throw new ResponseException(400, exceptionString);
        }
        else if(params[0].isBlank()) {
            throw new ResponseException(400, exceptionString);
        }
        Integer trueGameIndex = validateGameNumber(exceptionString, params);
        BoardDrawer bd = new BoardDrawer(activeGame.getBoard());
        bd.drawChessBoard(whiteUsername, blackusername, activeGame.getTeamTurn(), ChessGame.TeamColor.WHITE, activeGame.getBoard());
        return SET_TEXT_COLOR_BLUE + "Observing game. Enjoy the show!";
    }

    public String help() {
        if (state == State.LOGGED_OUT) {
            return SET_TEXT_COLOR_YELLOW + """
                    - login <USERNAME> <PASSWORD>
                    - register <USERNAME> <PASSWORD> <EMAIL>
                    - help
                    - quit
                    """;
        } else if (state == State.PLAYING) {
            return SET_TEXT_COLOR_YELLOW + """
                    - help
                    - moves c,r
                        - Where r and c are the row and column of the piece you'd like to see the moves of
                    - move r1,c1 r2,c2
                        - Where r1 and c1 are the row and column of the moving piece,
                          and r2 c2 are the row and column where you'd like to move the piece
                    - redraw
                    - leave
                    - resign
                    """;
        }  else if (state == State.OBSERVING) {
            return SET_TEXT_COLOR_YELLOW + """
                    - help
                    - redraw
                    - leave
                    """;
        }
        return SET_TEXT_COLOR_YELLOW + """
                - help
                - logout
                - create <GAMENAME>
                - list
                - play <GAMEID#> <COLOR>
                - observe <GAMEID#>
                - quit
                """;
    }

    private void validateUserCoord(String... args) throws ResponseException {
        String exceptionString;
        String col;
        try {
            Integer.parseInt(args[1]);
        } catch(NumberFormatException e) {
            exceptionString = SET_TEXT_COLOR_RED + "Expected: c,r \n where r is a number (1-8) and c is a letter (a-h)";
            throw new ResponseException(400, exceptionString);
        }
        col = args[0].toLowerCase();
        char colChar = col.charAt(0);
        if(colChar > 'h' || colChar < 'a' || col.length() > 1) {
            exceptionString = SET_TEXT_COLOR_RED + "Expected: c,r \n where r is a number (1-8) and c is a letter (a-h)";
            throw new ResponseException(400, exceptionString);
        }
    }

    private Integer validateGameNumber(String exceptionString, String[] params) throws ResponseException {
        Integer trueGameIndex;
        try {
            Integer providedGameIndex = Integer.parseInt(params[0]);
            trueGameIndex = gameList.get(providedGameIndex);
            if(trueGameIndex == null) {
                throw new ResponseException(400, exceptionString + SET_TEXT_ITALIC + " must be number from the list");
            }
        } catch (NumberFormatException | ResponseException e) {
            throw new ResponseException(400, exceptionString + SET_TEXT_ITALIC + " must be number from the list");
        }
        return trueGameIndex;
    }
    private void assertSignedIn() throws ResponseException {
        if (state == State.LOGGED_OUT) {
            throw new ResponseException(400, "You must sign in");
        }
    }
}
