package websocket.commands;

import chess.ChessMove;

public class JoinObserverCommand extends UserGameCommand{
    public JoinObserverCommand(CommandType commandType, String token, int gameID, ChessMove move) {
        super(commandType, token, gameID);
    }
}
