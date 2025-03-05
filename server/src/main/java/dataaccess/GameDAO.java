package dataaccess;

import results.DataAccessResult;

interface GameDAO {
    DataAccessResult getGames() throws DataAccessException;
    DataAccessResult createGame(String gameName) throws DataAccessException;
    DataAccessResult isColorAvailable(int gameID, String color) throws DataAccessException;
    DataAccessResult joinGame(int gameID, String color, String username) throws DataAccessException;
    DataAccessResult isGameNumberValid(int gameID) throws DataAccessException;;
}
