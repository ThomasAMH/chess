package websocket.commands;

public class UserGameCommand {
    public enum CommandType {
        CONNECT_PLAYER,
        MAKE_MOVE,
        LEAVE,
        RESIGN,
        CONNECT_OBSERVER
    }

    public CommandType commandType;
    public String authToken;
    public Integer gameID;
    public UserGameCommand(CommandType commandType, String token, int gameID) {
        this.commandType = commandType;
        this.authToken = token;
        this.gameID = gameID;
    }
}
