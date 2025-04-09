package dataaccess;

import chess.ChessGame;
import dbobjects.GameRecord;
import model.GameData;
import results.DataAccessResult;

import javax.xml.crypto.Data;
import java.util.ArrayList;

interface GameDAO {
    ArrayList<GameData> getGames() throws DataAccessException;
    int createGame(String gameName) throws DataAccessException;
    DataAccessResult isColorAvailable(int gameID, String color) throws DataAccessException;
    DataAccessResult joinGame(int gameID, String color, String username) throws DataAccessException;
    DataAccessResult isGameNumberValid(int gameID) throws DataAccessException;;
    DataAccessResult getUsernameByGameID(int gameID, ChessGame.TeamColor color) throws DataAccessException;
    GameRecord getGameByID(Integer gameID) throws DataAccessException;
    void setGameByID(Integer gameID, String gameJson) throws DataAccessException;
}
