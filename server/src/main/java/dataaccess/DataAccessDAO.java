package dataaccess;

import chess.ChessGame;
import dbobjects.GameRecord;
import model.GameData;
import model.UserData;
import model.AuthData;
import requests.RegisterRequest;
import results.DataAccessResult;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public abstract class DataAccessDAO {
    public UserDataDAO userData;
    public AuthDataDAO authData;
    public GameDataDAO gameData;

    public DataAccessDAO() {
        this.userData = new UserDataDAO();
        this.authData = new AuthDataDAO();
        this.gameData = new GameDataDAO();
    }

    protected abstract boolean daoDoesUserExist(String username);
    protected abstract UserData daoGetUserData(String username);
    protected abstract boolean daoSaveNewUser(UserData userData);
    protected abstract boolean daoIsPasswordValid(UserData userData);

    protected abstract boolean daoStoreAuthToken(AuthData data);
    protected abstract boolean daoContainsAuthToken(String token);
    protected abstract void daoDeleteAuthToken(String token);

    protected abstract void daoJoinGame(int gameID, String color, String authToken);
    protected abstract ArrayList<GameData> daoGetGames();
    protected abstract int daoAddGame(String gameName);
    protected abstract boolean daoIsTeamColorFree(int gameID, String color);
    protected abstract String daoGetUsernameFromAuthToken(String authToken);
    protected abstract boolean daoIsGameNumberValid(int gameID);
    protected abstract String daoGetPlayerUsername(int gameID, ChessGame.TeamColor color);
    protected abstract GameRecord daoGetGameByID(Integer gameID);
    protected abstract void daoUpdateGameByID(Integer gameID, String gameJson);

    public abstract void nukeEverything();
    public abstract void clearDatabase();

    public class UserDataDAO implements UserDAO {
        @Override
        public DataAccessResult doesUserExist(String username) throws DataAccessException {
            if(daoDoesUserExist(username)) {
                return new DataAccessResult("true");
            }
            return new DataAccessResult("false");
        }
        public DataAccessResult createUser(RegisterRequest request) throws DataAccessException {
            UserData newUser = new UserData(request.username(), request.password(), request.email());
            if (daoSaveNewUser(newUser)) {
                return new DataAccessResult("User created. Request data saved.");
            } else {
                throw new DataAccessException("Error encountered in saving user data");
            }

        }

        @Override
        public DataAccessResult getPassword(String username) throws DataAccessException {
            if(!daoDoesUserExist(username)) {
                throw new DataAccessException("Error: username does not exist!");
            }
            return new DataAccessResult(daoGetUserData(username).password());
        }

        public Boolean isPasswordValid(String username, String password) throws DataAccessException {
            if(!daoDoesUserExist(username)) {
                throw new DataAccessException("Error: username does not exist!");
            }
            UserData userData = new UserData(username, password, null);
            return daoIsPasswordValid(userData);
        }
    }
    public class AuthDataDAO implements AuthDAO {
        public DataAccessResult getAuthToken(String username) throws DataAccessException {
            String token = UUID.randomUUID().toString();
            AuthData newData = new AuthData(token, username);
            daoStoreAuthToken(newData);
            return new DataAccessResult(token);
        }
        public DataAccessResult deleteAuthToken(String token) throws DataAccessException {
            if(!daoContainsAuthToken(token)) {
                throw new DataAccessException("Provided token not associated with any logged in users.");
            }
            daoDeleteAuthToken(token);
            return new DataAccessResult("Deletion successful");
        }

        @Override
        public DataAccessResult doesAuthTokenExist(String token) throws DataAccessException {
            if(!daoContainsAuthToken(token)) {
                return new DataAccessResult("false");
            }
            return new DataAccessResult("true");
        }

        @Override
        public DataAccessResult getUserFromAuthToken(String token) throws DataAccessException {
            return new DataAccessResult(daoGetUsernameFromAuthToken(token));
        }
    }
    public class GameDataDAO implements GameDAO {
        @Override
        public GameRecord getGameByID(Integer gameID) {
            return daoGetGameByID(gameID);
        }

        @Override
        public void setGameByID(Integer gameID, String gameJson) throws DataAccessException {
            daoUpdateGameByID(gameID, gameJson);
        }

        @Override
        public ArrayList<GameData> getGames() throws DataAccessException {
            return daoGetGames();
        }

        @Override
        public int createGame(String gameName) throws DataAccessException {
            return daoAddGame(gameName);
        }

        @Override
        public DataAccessResult isColorAvailable(int gameID, String color) throws DataAccessException {
            if(daoIsTeamColorFree(gameID, color)) {
                return new DataAccessResult("true");
            } else {
                return new DataAccessResult("false");
            }
        }

        @Override
        public DataAccessResult joinGame(int gameID, String color, String authToken) throws DataAccessException {
            String username = daoGetUsernameFromAuthToken(authToken);
            String targetUsername;
            if(Objects.equals(color, "white")) {
                targetUsername = daoGetPlayerUsername(gameID, ChessGame.TeamColor.WHITE);
            } else {
                targetUsername = daoGetPlayerUsername(gameID, ChessGame.TeamColor.BLACK);
            }

            if(daoContainsAuthToken(authToken)) {
                daoJoinGame(gameID, color, authToken);
                return new DataAccessResult("true");
            }
            return new DataAccessResult("false");
        }

        @Override
        public DataAccessResult isGameNumberValid(int gameID) throws DataAccessException {
            if(daoIsGameNumberValid(gameID)) {
                return new DataAccessResult("true");
            } else {
                return new DataAccessResult("false");
            }
        }

        @Override
        public DataAccessResult getUsernameByGameID(int gameID, ChessGame.TeamColor color) throws DataAccessException {
            return new DataAccessResult(daoGetPlayerUsername(gameID, color));
        }
    }
}
