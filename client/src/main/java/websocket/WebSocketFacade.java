package websocket;

import chess.ChessGame;
import chess.ChessGameTypeAdapter;
import chess.ChessMove;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import exceptions.ResponseException;
import ui.Client;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;


//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;
    Client callingClient;
    GsonBuilder chessGameBuilder;


    public WebSocketFacade(String url, NotificationHandler notificationHandler, Client callingClient) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;
            this.callingClient = callingClient;
            chessGameBuilder = new GsonBuilder();
            chessGameBuilder.registerTypeAdapter(ChessGame.class, new ChessGameTypeAdapter());

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                    if (Objects.requireNonNull(notification.serverMessageType) == ServerMessage.ServerMessageType.LOAD_GAME) {
                        Gson gson = chessGameBuilder.create();
                        ChessGame game = gson.fromJson(notification.message, ChessGame.class);
                        callingClient.setGame(game);
                        notificationHandler.notify(notification);
                    } else {
                        notificationHandler.notify(notification);
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void joinGame(String token, int gameId) throws ResponseException {
        sendGameStatusCommand(UserGameCommand.CommandType.CONNECT_PLAYER, token, gameId);
    }

    public void leaveGame(String token, int gameId) throws ResponseException {
        sendGameStatusCommand(UserGameCommand.CommandType.LEAVE, token, gameId);
    }

    public void resignGame(String token, int gameId) throws ResponseException {
        sendGameStatusCommand(UserGameCommand.CommandType.RESIGN, token, gameId);
    }

    public void makeMove(String token, int gameId, ChessMove move) throws ResponseException {
        try {
            var command = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, token, gameId, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private void sendGameStatusCommand(UserGameCommand.CommandType commandType, String token, int gameID) throws ResponseException{
        try {
            var command = new UserGameCommand(commandType, token, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }


}