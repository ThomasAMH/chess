package results;
import chess.ChessGame;
import java.util.ArrayList;

public record ListGamesResult(int responseCode, String responseMessage, String games) {
}
