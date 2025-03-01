package dataaccess;

import results.DataAccessResult;

interface GameDAO {
    DataAccessResult requestGames();
    DataAccessResult createGame(String gameName);
    DataAccessResult checkGameAvailability(String gameID, String color) throws DataAccessException;
    DataAccessResult joinGame(String gameID, String color, String username);
    private int getNewGameID() {
        return 0;
    }
}
