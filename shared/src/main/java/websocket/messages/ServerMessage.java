package websocket.messages;

public class ServerMessage {
    public String message;
    public ServerMessageType serverMessageType;
    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    public ServerMessage(ServerMessageType type, String message) {
        this.serverMessageType = type;
        this.message = message;
    }
}
