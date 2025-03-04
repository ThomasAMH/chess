package dataaccess;

import results.DataAccessResult;

interface GameDAO {
    DataAccessResult getGames() throws DataAccessException;
    DataAccessResult createGame(String gameName) throws DataAccessException;
    DataAccessResult checkGameAvailability(String gameID, String color) throws DataAccessException;
    DataAccessResult joinGame(String gameID, String color, String username) throws DataAccessException;
}
