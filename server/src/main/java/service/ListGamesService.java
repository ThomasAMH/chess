package service;

import chess.ChessGame;
import results.ListGamesResult;

import java.util.ArrayList;

public class ListGamesService {
    ListGamesResult listGames(ListGamesService request) {
        ListGamesResult result = new ListGamesResult(200, "test message", new ArrayList<ChessGame>());
        //authenticateUser

        return result;
    }
}
