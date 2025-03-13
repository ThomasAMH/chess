package dataaccess;

import model.GameData;
import model.UserData;
import model.AuthData;
import requests.RegisterRequest;
import results.DataAccessResult;

import java.util.ArrayList;
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
    protected abstract boolean daoContainsAuthToken(String username);
    protected abstract void daoDeleteAuthToken(String username);

    protected abstract ArrayList<GameData> daoGetGames();
    protected abstract int daoAddGame(String gameName);
    protected abstract boolean daoIsTeamColorFree(int gameID, String color);
    protected abstract void daoJoinGame(int gameID, String color, String username);
    protected abstract boolean daoIsGameNumberValid(int gameID);

    public abstract void nukeEverything();

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
    }
    public class GameDataDAO implements GameDAO {

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
        public DataAccessResult joinGame(int gameID, String color, String username) throws DataAccessException {
            daoJoinGame(gameID, color, username);
            return new DataAccessResult("true");
        }

        @Override
        public DataAccessResult isGameNumberValid(int gameID) throws DataAccessException {
            if(daoIsGameNumberValid(gameID)) {
                return new DataAccessResult("true");
            } else {
                return new DataAccessResult("false");
            }
        }


    }
}
