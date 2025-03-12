package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.*;


public class MemoryDAO extends DataAccessDAO {

    private HashMap<String, UserData> userDataHashmap;
    private HashMap<Integer, GameData> gameDataHashMap;
    private HashMap<String, AuthData> authDataHashmap;

    private int currentGameIndex = 0;

    public MemoryDAO() {
        this.userDataHashmap = new HashMap<String, UserData>();
        this.gameDataHashMap = new HashMap<Integer, GameData>();
        this.authDataHashmap = new HashMap<String, AuthData>();
    }

    @Override
    protected boolean daoDoesUserExist(String username) {
        return userDataHashmap.containsKey(username);
    };
    @Override
    protected UserData daoGetUserData(String username) {
        return userDataHashmap.get(username);
    };
    @Override
    protected boolean daoSaveNewUser(UserData userData) {
        userDataHashmap.put(userData.username(), userData);
        return true;
    };

    @Override
    protected void daoStoreAuthToken(AuthData data) {
        authDataHashmap.put(data.authToken(), data);
    };
    @Override
    protected boolean daoContainsAuthToken(String token) {
        return authDataHashmap.containsKey(token);
    };
    @Override
    protected void daoDeleteAuthToken(String username) {
        authDataHashmap.remove(username);
    }

    @Override
    protected ArrayList<GameData> daoGetGames() {
        return new ArrayList<GameData>(gameDataHashMap.values());
    }

    ;
    @Override
    protected int daoAddGame(String gameName) {
        ChessGame newGame = new ChessGame();
        int gameID = daoGetChessGameIndex();
        GameData game = new GameData(gameID, null, null, gameName, newGame);
        gameDataHashMap.put(gameID, game);
        return gameID;
    };

    @Override
    protected int daoGetChessGameIndex() {
          currentGameIndex++;
          return currentGameIndex;
    }

    @Override
    protected boolean daoIsTeamColorFree(int gameID, String color) {
        GameData data = gameDataHashMap.get(gameID);
        if(color.equals("WHITE")) {
            return data.whiteUsername() == null;
        } else {
            return data.blackUsername() == null;
        }
    }

    @Override
    protected void daoJoinGame(int gameID, String color, String username) {
        GameData oldData = gameDataHashMap.get(gameID);
        GameData newData;
        if(color.equals("WHITE")) {
            newData = new GameData(gameID, username, oldData.blackUsername(), oldData.gameName(), oldData.game());
        } else {
            newData = new GameData(gameID, oldData.whiteUsername(), username, oldData.gameName(), oldData.game());
        }
        gameDataHashMap.remove(gameID);
        gameDataHashMap.put(gameID, newData);
    }

    @Override
    protected boolean daoIsGameNumberValid(int gameID) {
        return gameDataHashMap.containsKey(gameID);
    }

    @Override
    public void nukeEverything() {
        userDataHashmap.clear();
        authDataHashmap.clear();
        gameDataHashMap.clear();
    }


}