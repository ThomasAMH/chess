package dataaccess;

import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestUserEndpoints {
    DataAccessDAO dataAccessDAO;

    public TestUserEndpoints() {
        try {
            dataAccessDAO = new DatabaseDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void saveNewUserTestPositive() {
        dataAccessDAO.daoSaveNewUser(new UserData("Tom", "12345", "tom@thebomb.com"));
        dataAccessDAO.daoSaveNewUser(new UserData("Brian", "12345", "brian@thebomb.com"));
        dataAccessDAO.daoSaveNewUser(new UserData("Peter", "12345", "pete@thebomb.com"));

        assertTrue(dataAccessDAO.daoDoesUserExist("Tom"));
        assertTrue(dataAccessDAO.daoDoesUserExist("Brian"));
        assertTrue(dataAccessDAO.daoDoesUserExist("Peter"));

        dataAccessDAO.nukeEverything();
    }

    @Test
    public void createUserTestNegative() {
        // Because the DAO does not check if data is good or not, the only test that must fail is the one
        // Where username, the non-nullable primary key, is null.
        dataAccessDAO.daoSaveNewUser(new UserData("Tom", null, "tom@thebomb.com"));
        dataAccessDAO.daoSaveNewUser(new UserData("Brian", "12345", null));
        dataAccessDAO.daoSaveNewUser(new UserData(null, "12345", "pete@thebomb.com"));

        assertTrue(dataAccessDAO.daoDoesUserExist("Tom"));
        assertTrue(dataAccessDAO.daoDoesUserExist("Brian"));
        assertFalse(dataAccessDAO.daoDoesUserExist("Peter"));
        dataAccessDAO.nukeEverything();
    }

    @Test
    public void doesUserExistTestPositive() {
        // Because the DAO does not check if data is good or not, the only test that must fail is the one
        // Where username, the non-nullable primary key, is null.
        dataAccessDAO.daoSaveNewUser(new UserData("Tom", "12345", "tom@thebomb.com"));
        assertTrue(dataAccessDAO.daoDoesUserExist("Tom"));
        dataAccessDAO.nukeEverything();
    }

    @Test
    public void doesUserExistTestNegative() {
        // Because the DAO does not check if data is good or not, the only test that must fail is the one
        // Where username, the non-nullable primary key, is null.
        dataAccessDAO.daoSaveNewUser(new UserData("Tom", "12345", "tom@thebomb.com"));

        assertFalse(dataAccessDAO.daoDoesUserExist("Marcell"));
        assertFalse(dataAccessDAO.daoDoesUserExist(null));
        dataAccessDAO.nukeEverything();
    }

    @Test
    public void getUserDataTestPositive() {
        // Password is hashed and will not be tested here
        dataAccessDAO.daoSaveNewUser(new UserData("Tom", "12345", "tom@thebomb.com"));
        UserData goodData = new UserData("Tom", "12345", "tom@thebomb.com");

        assertEquals(goodData.username(), dataAccessDAO.daoGetUserData("Tom").username());
        assertEquals(goodData.email(), dataAccessDAO.daoGetUserData("Tom").email());
        dataAccessDAO.nukeEverything();
    }

    @Test
    public void getUserDataTestNegative() {
        // Password is hashed and will not be tested here
        assertNull(dataAccessDAO.daoGetUserData("Bernadetta"));
        dataAccessDAO.nukeEverything();
    }

    @Test
    public void isPasswordValidPositive() {
        UserData testData = new UserData("Tom", "12345", "tom@thebomb.com");
        dataAccessDAO.daoSaveNewUser(testData);
        assertTrue(dataAccessDAO.daoIsPasswordValid(testData));
    }

    @Test
    public void isPasswordValidNegative() {
        UserData testData = new UserData("Tom", "12345", "tom@thebomb.com");
        dataAccessDAO.daoSaveNewUser(testData);
        UserData badData = new UserData("Tom", "67890", "tom@thebomb.com");
        assertFalse(dataAccessDAO.daoIsPasswordValid(badData));
    }
    /*
    protected abstract boolean daoStoreAuthToken(AuthData data);
    protected abstract boolean daoContainsAuthToken(String username);
    protected abstract void daoDeleteAuthToken(String username);

    protected abstract ArrayList<GameData> daoGetGames();
    protected abstract int daoAddGame(String gameName);
    protected abstract boolean daoIsTeamColorFree(int gameID, String color);
    protected abstract void daoJoinGame(int gameID, String color, String username);
    protected abstract boolean daoIsGameNumberValid(int gameID);

 */
}
