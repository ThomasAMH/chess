package dataaccess;

import model.GameData;
import results.DataAccessResult;

import java.util.ArrayList;

interface GameDAO {
    ArrayList<GameData> getGames() throws DataAccessException;
    int createGame(String gameName) throws DataAccessException;
    DataAccessResult isColorAvailable(int gameID, String color) throws DataAccessException;
    DataAccessResult joinGame(int gameID, String color, String username) throws DataAccessException;
    DataAccessResult isGameNumberValid(int gameID) throws DataAccessException;;
}
