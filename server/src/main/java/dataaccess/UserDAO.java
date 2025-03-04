package dataaccess;
import requests.RegisterRequest;
import results.DataAccessResult;

interface UserDAO{
    // How does the auth token work? Store with user?
    DataAccessResult doesUserExist(String username) throws DataAccessException;
    DataAccessResult isPasswordValid(RegisterRequest request) throws DataAccessException;
    DataAccessResult createUser(RegisterRequest request) throws DataAccessException;

    DataAccessResult getPassword(String username) throws DataAccessException;
}
