package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import results.DataAccessResult;

import java.util.*;


public class MemoryDAO extends DataAccessDAO {

    private HashMap<String, UserData> userDataHashmap;
    private HashMap<Integer, GameData> gameDataHashMap;
    private HashMap<String, AuthData> authDataHashmap;

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
    protected void daoSaveNewUser(UserData userData) {
        userDataHashmap.put(userData.username(), userData);
    };

    @Override
    protected void daoStoreAuthToken(AuthData data) {
        authDataHashmap.put(data.username(), data);
    };
    @Override
    protected boolean daoContainsAuthToken(String username) {
        return authDataHashmap.containsKey(username);
    };
    @Override
    protected String daoGetAuthToken(String username) {
        return authDataHashmap.get(username).authToken();
    };
    @Override
    protected void daoDeleteAuthToken(String username) {
        authDataHashmap.remove(username);
    };
}

//    public class GameDAO implements dataaccess.GameDAO {
//        public DataAccessResult requestGames() {
//            String games = new Gson().toJson(gameDataHashMap);
//            return new DataAccessResult(true, games);
//        }
//        public DataAccessResult createGame(String gameName) {
//            int gameID = getNewGameID();
//            GameData newGame = new GameData(gameID, "", "", gameName, new ChessGame());
//            gameDataHashMap.put(gameID, newGame);
//            return new DataAccessResult(true, "");
//        }
//        public DataAccessResult checkGameAvailability(String gameID, String color) throws DataAccessException {
//            int gameIDInt = Integer.parseInt(gameID);
//            if(!gameDataHashMap.containsKey(gameIDInt)) {
//                throw new DataAccessException("Invalid game ID!");
//            }
//            if(!Objects.equals(color, "WHITE") && !Objects.equals(color, "BLACK")) {
//                throw new DataAccessException("Invalid color!");
//            }
//
//            GameData targetGame = gameDataHashMap.get(gameIDInt);
//
//            if(color.equals("WHITE") && (!Objects.equals(targetGame.whiteUsername(), ""))) {
//                return new DataAccessResult(false, "Selected color in selected game is already taken.");
//            } else if(color.equals("BLACK") && (!Objects.equals(targetGame.blackUsername(), ""))) {
//                return new DataAccessResult(false, "Selected color in selected game is already taken.");
//            }
//
//            return new DataAccessResult(true, "Selected color in selected game is available.");
//        }
//        public DataAccessResult joinGame(String gameID, String color, String username) {
//            int gameIDInt = Integer.parseInt(gameID);
//            GameData currentGame = gameDataHashMap.get(gameIDInt);
//            GameData updatedGame;
//            if(color.equals("WHITE")) {
//                updatedGame = new GameData(gameIDInt, username, currentGame.blackUsername(), currentGame.gameName(), currentGame.game());
//            } else {
//                updatedGame = new GameData(gameIDInt, currentGame.whiteUsername(), username, currentGame.gameName(), currentGame.game());
//            }
//
//            gameDataHashMap.put(gameIDInt, updatedGame);
//
//            return new DataAccessResult(true, "Game joined.");
//        }
//        private int getNewGameID() {
//            return Collections.max(gameDataHashMap.keySet()) + 1;
//        }
//    };