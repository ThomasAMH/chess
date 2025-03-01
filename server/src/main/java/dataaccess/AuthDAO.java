package dataaccess;

import results.DataAccessResult;

interface AuthDAO {
    DataAccessResult authenticateUser(String username, String token);
}
