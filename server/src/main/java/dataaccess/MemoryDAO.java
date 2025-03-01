package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import results.DataAccessResult;

import java.util.*;


public class MemoryDAO implements DataAccessDAO {
    HashMap<String, UserData> userDataHashmap = new HashMap<String, UserData>();
    HashMap<Integer, GameData> gameDataHashMap = new HashMap<Integer, GameData>();
    HashMap<String, AuthData> authDataHashmap = new HashMap<String, AuthData>();

    MemoryDAO.AuthDAO authDAO = new MemoryDAO.AuthDAO();

    public class AuthDAO implements dataaccess.AuthDAO {
        public DataAccessResult getAuthToken(String username) {
            String token = UUID.randomUUID().toString();
            AuthData userData = new AuthData(token, username);
            authDataHashmap.put(username, userData);
            return new DataAccessResult(true, token);
        }
        public DataAccessResult deleteAuthToken(String username) throws DataAccessException {
            if(!authDataHashmap.containsKey(username)) {
                throw new DataAccessException("Username does not have associated authentication token!");
            }
            authDataHashmap.remove(username);
            return new DataAccessResult(true, "");
        }
    };
    public class GameDAO implements dataaccess.GameDAO {
        public DataAccessResult requestGames() {
            String games = new Gson().toJson(gameDataHashMap);
            return new DataAccessResult(true, games);
        }
        public DataAccessResult createGame(String gameName) {
            int gameID = getNewGameID();
            GameData newGame = new GameData(gameID, "", "", gameName, new ChessGame());
            gameDataHashMap.put(gameID, newGame);
            return new DataAccessResult(true, "");
        }
        public DataAccessResult checkGameAvailability(String gameID, String color) throws DataAccessException {
            int gameIDInt = Integer.parseInt(gameID);
            if(!gameDataHashMap.containsKey(gameIDInt)) {
                throw new DataAccessException("Invalid game ID!");
            }
            if(!Objects.equals(color, "WHITE") && !Objects.equals(color, "BLACK")) {
                throw new DataAccessException("Invalid color!");
            }

            GameData targetGame = gameDataHashMap.get(gameIDInt);

            if(color.equals("WHITE") && (!Objects.equals(targetGame.whiteUsername(), ""))) {
                return new DataAccessResult(false, "Selected color in selected game is already taken.");
            } else if(color.equals("BLACK") && (!Objects.equals(targetGame.blackUsername(), ""))) {
                return new DataAccessResult(false, "Selected color in selected game is already taken.");
            }

            return new DataAccessResult(true, "Selected color in selected game is available.");
        }
        public DataAccessResult joinGame(String gameID, String color, String username) {
            int gameIDInt = Integer.parseInt(gameID);
            GameData currentGame = gameDataHashMap.get(gameIDInt);
            GameData updatedGame;
            if(color.equals("WHITE")) {
                updatedGame = new GameData(gameIDInt, username, currentGame.blackUsername(), currentGame.gameName(), currentGame.game());
            } else {
                updatedGame = new GameData(gameIDInt, currentGame.whiteUsername(), username, currentGame.gameName(), currentGame.game());
            }

            gameDataHashMap.put(gameIDInt, updatedGame);

            return new DataAccessResult(true, "Game joined.");
        }
        private int getNewGameID() {
            return Collections.max(gameDataHashMap.keySet()) + 1;
        }
    };
    public class UserDAO {
        public DataAccessResult doesUserExist(String username) throws DataAccessException {
            if(userDataHashmap.containsKey(username)) {
                throw new DataAccessException("Username Taken!");
            }
            return new DataAccessResult(false, "Username is free.");
        }
        public DataAccessResult isPasswordValid(String username, String password) throws DataAccessException {
            if(!userDataHashmap.containsKey(username)) {
                throw new DataAccessException("Username does not exist!");
            }
            UserData userData = userDataHashmap.get(username);
            if(userData.password().equals(password)) {
                return new DataAccessResult(true, "Password is valid.");
            } else {
                return new DataAccessResult(false, "Password is valid.");
            }
        }
        public void createUser(String username, String password, String email) {
            UserData newUser = new UserData(username, password, email);
            userDataHashmap.put(username, newUser);
        }
    }
}
