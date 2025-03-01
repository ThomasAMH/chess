package dataaccess;
import results.DataAccessResult;

interface UserDAO {
    // How does the auth token work? Store with user?
    DataAccessResult doesUserExist(String username) throws DataAccessException;
    DataAccessResult isPasswordValid(String username, String password) throws DataAccessException;
    void createUser(String username, String password, String email);

    DataAccessResult deleteAuthToken(String username) throws DataAccessException;



}
