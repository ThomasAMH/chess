package websocket.commands;
import chess.ChessMove;
import com.google.gson.Gson;

public class MakeMoveCommand extends UserGameCommand {
    public ChessMove move;
    public MakeMoveCommand(CommandType commandType, String token, int gameID, ChessMove move) {
        super(commandType, token, gameID);
        this.move = move;
    }
}
