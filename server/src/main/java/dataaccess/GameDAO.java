package dataaccess;

import results.DataAccessResult;

interface GameDAO {
    DataAccessResult requestGames(String username);
    DataAccessResult createGame(String gameName);
    DataAccessResult checkGameAvailability(String gameID, String color);
    DataAccessResult joinGame(String gameID, String color);
}
