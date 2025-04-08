package dbobjects;

import chess.ChessGame;

public record GameRecord(String gameJSON, String whiteUsername, String blackUsername, String gameName, Integer gameID) {
}
