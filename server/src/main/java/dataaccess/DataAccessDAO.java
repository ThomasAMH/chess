package dataaccess;

import model.UserData;
import model.AuthData;
import requests.RegisterRequest;
import results.DataAccessResult;

import java.util.UUID;

public abstract class DataAccessDAO {
    public UserDataDAO userData;
    public AuthDataDAO authData;

    public DataAccessDAO() {
        this.userData = new UserDataDAO();
        this.authData = new AuthDataDAO();
    }

    protected abstract boolean daoDoesUserExist(String username);
    protected abstract UserData daoGetUserData(String username);
    protected abstract void daoSaveNewUser(UserData userData);

    protected abstract void daoStoreAuthToken(AuthData data);
    protected abstract boolean daoContainsAuthToken(String username);
    protected abstract String daoGetAuthToken(String username);
    protected abstract void daoDeleteAuthToken(String username);

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
            daoSaveNewUser(newUser);
            return new DataAccessResult("User created. Request data saved.");
        }

        @Override
        public DataAccessResult getPassword(String username) throws DataAccessException {
            if(!daoDoesUserExist(username)) {
                throw new DataAccessException("Error: username does not exist!");
            }
            return new DataAccessResult(daoGetUserData(username).password());
        }


        public DataAccessResult isPasswordValid(RegisterRequest request) throws DataAccessException {
            if(daoDoesUserExist(request.username())) {
                throw new DataAccessException("Error: username does not exist!");
            }
            UserData userData = daoGetUserData(request.username());
            if(userData.password().equals(request.password())) {
                return new DataAccessResult("Password is valid.");
            } else {
                return new DataAccessResult("Password is valid.");
            }
        }


    }
    public class AuthDataDAO implements AuthDAO {
        public DataAccessResult getAuthToken(String username) throws DataAccessException {
            String token = UUID.randomUUID().toString();
            AuthData newData = new AuthData(token, username);
            daoStoreAuthToken(newData);
            return new DataAccessResult(token);
        }
        public DataAccessResult deleteAuthToken(String username) throws DataAccessException {
            if(daoContainsAuthToken(username)) {
                throw new DataAccessException("Username does not have associated authentication token!");
            }
            daoDeleteAuthToken(username);
            return new DataAccessResult("Deletion successful");
        }
    }
}
