package dataaccess;

import results.DataAccessResult;

import javax.xml.crypto.Data;

interface AuthDAO {
    DataAccessResult getAuthToken(String username)  throws DataAccessException;
    DataAccessResult deleteAuthToken(String token) throws DataAccessException;
    DataAccessResult doesAuthTokenExist(String token) throws DataAccessException;
    DataAccessResult getUserFromAuthToken(String token) throws DataAccessException;
}
