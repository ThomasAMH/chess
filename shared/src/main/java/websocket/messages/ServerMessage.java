package websocket.messages;

public class ServerMessage {
    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }
    ServerMessageType serverMessageType;
}
