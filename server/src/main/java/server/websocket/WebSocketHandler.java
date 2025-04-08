package server.websocket;

import com.google.gson.Gson;
import dataaccess.DataAccessDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseDAO;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;


@WebSocket
public class WebSocketHandler {
    private final DataAccessDAO dataAccessDAO;
    private final ConnectionManager connections = new ConnectionManager();
    public WebSocketHandler() throws DataAccessException {
        this.dataAccessDAO = new DatabaseDAO();
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.commandType) {
            case CONNECT_PLAYER -> enter(command, session);
//            case CONNECT_OBSERVER -> enter(command, session);
//            case MAKE_MOVE -> enter(command, session);
//            case LEAVE -> enter(command, session);
//            case RESIGN -> enter(command, session);
        }
    }

    private void enter(UserGameCommand command, Session session) throws IOException {
        connections.add(session, command.authToken, command.gameID);
        String username;
        try {
            username = dataAccessDAO.authData.getUserFromAuthToken(command.authToken).data();
        } catch (DataAccessException e) {
            throw new IOException();
        }
        String message = String.format("%s has joined the game", username);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(command.authToken, notification, command.gameID);
        String game = dataAccessDAO.gameData.GetGameByID(command.gameID).gameJSON();
        ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
        session.getRemote().sendString(new Gson().toJson(serverMessage, ServerMessage.class));
    }

//    private void exit(String visitorName) throws IOException {
//        connections.remove(visitorName);
//        var message = String.format("%s left the shop", visitorName);
//        var notification = new Notification(Notification.Type.DEPARTURE, message);
//        connections.broadcast(visitorName, notification);
//    }

}