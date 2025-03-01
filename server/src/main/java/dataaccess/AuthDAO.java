package dataaccess;

import results.DataAccessResult;

interface AuthDAO {
    DataAccessResult getAuthToken(String username);
    DataAccessResult deleteAuthToken(String username) throws DataAccessException;
}
