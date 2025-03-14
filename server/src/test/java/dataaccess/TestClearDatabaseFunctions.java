package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Test;
import server.Server;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestClearDatabaseFunctions {
    DataAccessDAO dataAccessDAO;

    public TestClearDatabaseFunctions() {
        try {
            dataAccessDAO = new DatabaseDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    public void nukeDatabaseTest() {
        //This is somewhat difficult to test due to the layers protecting the database, but any method to read the db
        //will return false
        dataAccessDAO.nukeEverything();
        dataAccessDAO.daoSaveNewUser(new UserData("Tom", "12345", "2@2.com"));
        assertFalse(dataAccessDAO.daoDoesUserExist("Tom"));
    }

    @Test
    public void clearDatabaseTest() {
        dataAccessDAO.daoSaveNewUser(new UserData("Tom", "12345", "2@2.com"));
        assertTrue(dataAccessDAO.daoDoesUserExist("Tom"));
        dataAccessDAO.clearDatabase();
        assertFalse(dataAccessDAO.daoDoesUserExist("Tom"));
    }
}
