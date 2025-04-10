package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataaccess.DataAccessDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseDAO;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
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
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.commandType) {
            case CONNECT_PLAYER -> enterPlayer(command, session);
            case MAKE_MOVE -> makeMove(message, session);
            case CONNECT_OBSERVER -> enterObserver(command, session);
            case LEAVE -> leave(command, session);
            case RESIGN -> resign(command, session);
        }
    }

    private void enterPlayer(UserGameCommand command, Session session) throws IOException {
        connections.add(session, command.authToken, command.gameID);
        String username;
        try {
            username = dataAccessDAO.authData.getUserFromAuthToken(command.authToken).data();
        } catch (DataAccessException e) {
            throw new IOException();
        }
        String message = String.format("%s has joined the game", username);
        broadcastMessage(command, message);
        returnGame(command, session);
    }

    private void enterObserver(UserGameCommand command, Session session) throws IOException {
        connections.add(session, command.authToken, command.gameID);
        String username;
        try {
            username = dataAccessDAO.authData.getUserFromAuthToken(command.authToken).data();
        } catch (DataAccessException e) {
            throw new IOException();
        }
        String message = String.format("%s is observing the game", username);
        broadcastMessage(command, message);
        returnGame(command, session);
    }


    private void leave(UserGameCommand command, Session session) throws IOException {
        connections.remove(command.authToken);
        String username;
        try {
            username = dataAccessDAO.authData.getUserFromAuthToken(command.authToken).data();
        } catch (DataAccessException e) {
            throw new IOException();
        }
        String message = String.format("%s has left the game", username);
        broadcastMessage(command, message);
    }

    private void resign(UserGameCommand command, Session session) throws IOException {
        connections.remove(command.authToken);
        String username;
        try {
            username = dataAccessDAO.authData.getUserFromAuthToken(command.authToken).data();
        } catch (DataAccessException e) {
            throw new IOException();
        }
        String message = String.format("%s has resigned from game", username);
        broadcastMessage(command, message);
    }

    private void makeMove(String message, Session session) throws IOException {
        MakeMoveCommand command = new Gson().fromJson(message, MakeMoveCommand.class);
        String username;
        try {
            username = dataAccessDAO.authData.getUserFromAuthToken(command.authToken).data();
        } catch (DataAccessException e) {
            throw new IOException();
        }

        String game = dataAccessDAO.gameData.getGameByID(command.gameID).gameJSON();
        Gson gson = chessGameBuilder.create();
        ChessGame chessGame = gson.fromJson(game, ChessGame.class);
        try {
            chessGame.makeMove(new Gson().fromJson(command.chessMoveJson, ChessMove.class));
        } catch (InvalidMoveException e) {
            throw new RuntimeException(e);
        }
        String chessGameGson = new Gson().toJson(chessGame, ChessGame.class);
        try {
            dataAccessDAO.gameData.setGameByID(command.gameID, chessGameGson);
        } catch(DataAccessException ignored) {

        }

        ServerMessage broadcastGameMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, chessGameGson);
        connections.broadcastGame(broadcastGameMessage, command.gameID);

        String broadcastMsg = String.format("%s has moved", username);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, broadcastMsg);
        connections.broadcast(command.authToken, notification, command.gameID);
    }

    private void broadcastMessage(UserGameCommand command, String message) throws IOException {
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(command.authToken, notification, command.gameID);
    }

    private void returnGame(UserGameCommand command, Session session) throws IOException {
        String game = dataAccessDAO.gameData.getGameByID(command.gameID).gameJSON();
        ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
        session.getRemote().sendString(new Gson().toJson(serverMessage, ServerMessage.class));
    }

}