package returns;

import chess.ChessGame;
import model.GameMetaData;

import java.util.ArrayList;

public record ListGamesReturn(int status, ArrayList<GameMetaData> games) {
}
