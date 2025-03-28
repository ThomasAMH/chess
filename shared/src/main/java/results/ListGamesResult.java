package results;
import chess.ChessGame;
import model.GameData;
import model.GameMetaData;

import java.util.ArrayList;

public record ListGamesResult(int responseCode, String responseMessage, ArrayList<GameData> games) {
}
