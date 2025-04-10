package websocket.messages;

public class ServerMessage {
    public String message;
    public String game;
    public String errorMessage;
    public ServerMessageType serverMessageType;

    public ServerMessage(ServerMessageType type, String string) {
        this.message = null;
        this.game = null;
        this.errorMessage = null;
        this.serverMessageType = type;
        switch(type) {
            case NOTIFICATION:
                this.message = string;
                break;
            case LOAD_GAME:
                this.game = string;
                break;
            case ERROR:
                this.errorMessage = string;
                break;
        }
    }

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }
}
