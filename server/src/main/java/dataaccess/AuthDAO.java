package dataaccess;

import results.DataAccessResult;

interface AuthDAO {
    DataAccessResult getAuthToken(String username)  throws DataAccessException;
    DataAccessResult deleteAuthToken(String username) throws DataAccessException;
}
