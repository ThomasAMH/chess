package returns;

import chess.ChessGame;
import model.GameMetaData;

import java.util.ArrayList;

public record ListGamesReturn(ArrayList<GameMetaData> games) {
}
