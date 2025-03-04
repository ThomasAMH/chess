package dataaccess;

import results.DataAccessResult;

interface AuthDAO {
    DataAccessResult getAuthToken(String username)  throws DataAccessException;
    DataAccessResult deleteAuthToken(String token) throws DataAccessException;
    DataAccessResult doesAuthTokenExist(String token) throws DataAccessException;
}
