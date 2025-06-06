package server.websocket;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataaccess.DataAccessDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseDAO;
import dbobjects.GameRecord;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import server.Server;
import websocket.commands.JoinObserverCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Objects;

import chess.ChessGameTypeAdapter;

@WebSocket
public class WebSocketHandler {
    private final DataAccessDAO dataAccessDAO;
    private final ConnectionManager connections = new ConnectionManager();
    private final GsonBuilder chessGameBuilder;
    public WebSocketHandler() throws DataAccessException {
        this.dataAccessDAO = new DatabaseDAO();
        chessGameBuilder = new GsonBuilder();
        chessGameBuilder.registerTypeAdapter(ChessGame.class, new ChessGameTypeAdapter());
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        try {
            UserGameCommand command;
            if(message.contains("join")) {
                command = new Gson().fromJson(message, JoinObserverCommand.class);
            } else {
                command = new Gson().fromJson(message, UserGameCommand.class);
            }

            if(!validateAuthToken(command.authToken)) {
                sendErrorMessage("Bad auth token", session);
                return;
            }
            switch (command.commandType) {
                case CONNECT:
                    if(command.getClass() == JoinObserverCommand.class) {
                        enterObserver(command, session);
                    } else {
                        enterPlayer(command, session);
                    }
                    break;
                case MAKE_MOVE:
                    makeMove(message, session);
                    break;
                case LEAVE:
                    leave(command, session);
                    break;
                case RESIGN:
                    resign(command, session);
                    break;
            }
        } catch (NullPointerException e) {
            sendErrorMessage("Problem encountered while accessing database", session);
        }
    }

    private void enterPlayer(UserGameCommand command, Session session) throws IOException {
        String username;
        try {
            username = dataAccessDAO.authData.getUserFromAuthToken(command.authToken).data();
            GameRecord testID = dataAccessDAO.gameData.getGameByID(command.gameID);
            if(testID == null) {
                sendErrorMessage("Bad Game ID", session);
                return;
            }
            String message = String.format("%s has joined the game", username);
            broadcastMessage(command, message);
            returnGame(command, session);
        } catch (NullPointerException | DataAccessException e) {
            sendErrorMessage("Problem encountered while accessing database.", session);
        }
        connections.add(session, command.authToken, command.gameID);
    }

    private void enterObserver(UserGameCommand command, Session session){
        String username;
        try {
            username = dataAccessDAO.authData.getUserFromAuthToken(command.authToken).data();
            GameRecord testID = dataAccessDAO.gameData.getGameByID(command.gameID);
            String message = String.format("%s is observing the game", username);
            broadcastMessage(command, message);
            returnGame(command, session);
        } catch (DataAccessException | IOException e) {
            sendErrorMessage("Problem encountered while accessing database.", session);
        }
        connections.add(session, command.authToken, command.gameID);
    }

    private void leave(UserGameCommand command, Session session) throws IOException {
        connections.remove(command.authToken);
        String username;
        try {
            String isGameActive = dataAccessDAO.gameData.isGameNumberValid(command.gameID).data();
            if(Objects.equals(isGameActive, "false")) {
                return;
            }
            GameRecord gameData = dataAccessDAO.gameData.getGameByID(command.gameID);
            String blackUsername = gameData.blackUsername();
            String whiteUsername = gameData.whiteUsername();
            username = dataAccessDAO.authData.getUserFromAuthToken(command.authToken).data();
            String message;
            if(Objects.equals(username, blackUsername) || Objects.equals(username, whiteUsername)) {
                message = String.format("%s has left the game", username);
                broadcastMessage(command, message);
                dataAccessDAO.gameData.removeUserFromGame(command.gameID, username);
            } else {
                message = String.format("%s is no longer observing", username);
                broadcastMessage(command, message);
            }
        } catch (DataAccessException e) {
            throw new IOException();
        }
    }

    private void resign(UserGameCommand command, Session session) throws IOException {
        String username = "";
        try {
            username = dataAccessDAO.authData.getUserFromAuthToken(command.authToken).data();
            GameRecord gameData = dataAccessDAO.gameData.getGameByID(command.gameID);
            String blackUsername = gameData.blackUsername();
            String whiteUsername = gameData.whiteUsername();
            dataAccessDAO.gameData.deleteGameByID(command.gameID);
            if(!Objects.equals(username, blackUsername) && !Objects.equals(username, whiteUsername)) {
                sendErrorMessage("Problem encountered while accessing database.", session);
                return;
            }
        } catch (DataAccessException e) {
            sendErrorMessage("Problem encountered while accessing database.", session);
            return;
        }

        String message = String.format("%s has resigned from game. Type leave to leave.", username);
        ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(serverMessage, command.gameID);
        connections.remove(command.authToken);
    }

    private void makeMove(String message, Session session) throws IOException {
        MakeMoveCommand command = new Gson().fromJson(message, MakeMoveCommand.class);
        String username = "";
        try {
            username = dataAccessDAO.authData.getUserFromAuthToken(command.authToken).data();
        } catch (DataAccessException e) {
            sendErrorMessage("Problem encountered while accessing database.", session);
            return;
        }

        String game = dataAccessDAO.gameData.getGameByID(command.gameID).gameJSON();
        Gson gson = chessGameBuilder.create();
        ChessGame chessGame = gson.fromJson(game, ChessGame.class);

        ChessGame.TeamColor activePlayerTeam = chessGame.getTeamTurn();
        GameRecord gameData = dataAccessDAO.gameData.getGameByID(command.gameID);
        String blackUsername = gameData.blackUsername();
        String whiteUsername = gameData.whiteUsername();
        String activePlayerUsername;
        //Test if username's team matches piece color
        activePlayerUsername = (activePlayerTeam == ChessGame.TeamColor.WHITE) ? whiteUsername : blackUsername;

        if(!Objects.equals(username, activePlayerUsername)) {
            sendErrorMessage("Attempt to move another team's piece", session);
            return;
        }

        if(chessGame.gameState == ChessGame.GameState.CHECKMATE) {
            sendErrorMessage("Cannot move - checkmate", session);
            return;
        }

        try {
            chessGame.makeMove(command.move);
        } catch (InvalidMoveException e) {
            sendErrorMessage("Invalid move", session);
            return;
        }
        String chessGameGson = new Gson().toJson(chessGame, ChessGame.class);

        try {
            dataAccessDAO.gameData.setGameByID(command.gameID, chessGameGson);
        } catch(DataAccessException e) {
            sendErrorMessage("Problem encountered while accessing database.", session);
        }

        ServerMessage broadcastGameMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, chessGameGson);
        connections.broadcast(broadcastGameMessage, command.gameID);

        String moveFromCoord, moveToCoord;
        moveFromCoord = getColLetterFromInt(command.move.getStartPosition().getColumn()) +
                String.valueOf(command.move.getStartPosition().getRow());
        moveToCoord = getColLetterFromInt(command.move.getEndPosition().getColumn()) +
                String.valueOf(command.move.getEndPosition().getRow());


        String broadcastMsg = String.format("%s has moved: " + moveFromCoord + " -> " + moveToCoord, username);
        ServerMessage notification;
        String opponent = ((Objects.equals(username, blackUsername))) ? whiteUsername: blackUsername;
        if(chessGame.gameState == ChessGame.GameState.CHECK) {
            broadcastMsg = broadcastMsg + "; Check for " + opponent + "!";
        } else if (chessGame.gameState == ChessGame.GameState.CHECKMATE) {
            broadcastMsg = broadcastMsg + "; Checkmate for " + opponent + "!";
        } else {
            broadcastMsg = broadcastMsg;
        }
        notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, broadcastMsg);
        connections.exclusiveBroadcast(notification, command.gameID, command.authToken);

    }

    private void broadcastMessage(UserGameCommand command, String message) throws IOException {
            var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(notification, command.gameID);
    }

    private void returnGame(UserGameCommand command, Session session) throws IOException {
        String game = dataAccessDAO.gameData.getGameByID(command.gameID).gameJSON();
        ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
        session.getRemote().sendString(new Gson().toJson(serverMessage, ServerMessage.class));
    }

    private void sendErrorMessage(String message, Session session) {
        try {
            ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, message);
            session.getRemote().sendString(new Gson().toJson(serverMessage, ServerMessage.class));
        } catch (IOException ignore) {
        }
    }

    private boolean validateAuthToken(String token) {
        try {
            if(Objects.equals(dataAccessDAO.authData.doesAuthTokenExist(token).data(), "false")) {
                return false;
            }
        } catch (DataAccessException e) {
            return false;
        }
        return true;
    }

    private char getColLetterFromInt(int colInt) {
        return (char) ('A' + (colInt - 1));
    }
}