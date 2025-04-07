package websocket.commands;
import chess.ChessMove;
import com.google.gson.Gson;

public class MakeMoveCommand extends UserGameCommand {
    String chessMoveJson;
    public MakeMoveCommand(CommandType commandType, String token, int gameID, ChessMove move) {
        super(commandType, token, gameID);
        this.chessMoveJson = new Gson().toJson(move, ChessMove.class);
    }
}
