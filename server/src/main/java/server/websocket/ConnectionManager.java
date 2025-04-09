package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<String, Integer> gameData = new ConcurrentHashMap<>();

    public void add(Session session, String authToken, Integer gameID) {
        var connection = new Connection(authToken, session);
        connections.put(authToken, connection);
        gameData.put(authToken, gameID);
    }

    public void remove(String token) {
        connections.remove(token);
        gameData.remove(token);
    }

    public void broadcastGame(ServerMessage gameMessage, Integer gameID) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (!Objects.equals(gameData.get(c.authToken), gameID)) {
                continue;
            }
            if (c.session.isOpen()) {
                c.send(new Gson().toJson(gameMessage, ServerMessage.class));
            } else {
                removeList.add(c);
            }
        }
        for (var c : removeList) {
            remove(c.authToken);
        }
    }

    public void broadcast(String excludeToken, ServerMessage notification, Integer gameID) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (!Objects.equals(gameData.get(c.authToken), gameID)) {
                continue;
            }
            if (c.session.isOpen()) {
                if (!c.authToken.equals(excludeToken)) {
                    c.send(new Gson().toJson(notification, ServerMessage.class));
                }
            } else {
                removeList.add(c);
            }
        }
        for (var c : removeList) {
            remove(c.authToken);
        }
    }
}