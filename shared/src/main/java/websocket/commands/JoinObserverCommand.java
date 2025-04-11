package websocket.commands;

import chess.ChessMove;

public class JoinObserverCommand extends UserGameCommand{
    boolean joinFlag;
    public JoinObserverCommand(CommandType commandType, String token, int gameID) {
        super(commandType, token, gameID);
        this.joinFlag = true;
    }
}
