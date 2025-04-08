package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<String, Integer> gameData = new ConcurrentHashMap<>();

    public void add( Session session, String authToken, Integer gameID) {
        var connection = new Connection(authToken, session);
        connections.put(authToken, connection);
        gameData.put(authToken, gameID);
    }

    public void remove(String token) {
        connections.remove(token);
        gameData.remove(token);
    }

    public void broadcast(String excludeToken, ServerMessage notification, Integer gameID) throws IOException {

        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (!Objects.equals(gameData.get(c.authToken), gameID)) {
                continue;
            }
            if (c.session.isOpen()) {
                if (!c.authToken.equals(excludeToken)) {
                    c.send(notification.toString());
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c.authToken);
        }
    }
}