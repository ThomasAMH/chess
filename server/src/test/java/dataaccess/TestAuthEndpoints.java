package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestAuthEndpoints {
    DataAccessDAO dataAccessDAO;

    public TestAuthEndpoints() {
        try {
            dataAccessDAO = new DatabaseDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void storeAuthTokenPositive() {
        assertTrue(dataAccessDAO.daoStoreAuthToken(new AuthData("12345", "Tom")));
    }

    @Test
    public void storeAuthTokenNegative() {
        assertTrue(dataAccessDAO.daoStoreAuthToken(new AuthData("12345", "Tom")));
        assertFalse(dataAccessDAO.daoStoreAuthToken(new AuthData("12345", "Tom")));
    }

    @Test
    public void containsAuthTokenPositive() {
        dataAccessDAO.daoStoreAuthToken(new AuthData("12345", "Tom"));
        assertTrue(dataAccessDAO.daoContainsAuthToken("12345"));
    }

    @Test
    public void containsAuthTokenNegative() {
        dataAccessDAO.daoStoreAuthToken(new AuthData("12345", "Tom"));
        assertFalse(dataAccessDAO.daoContainsAuthToken("ABC"));
    }

    @Test
    public void deleteAuthTokenPositive() {
        dataAccessDAO.daoStoreAuthToken(new AuthData("12345", "Tom"));
        dataAccessDAO.daoDeleteAuthToken("12345");
        assertFalse(dataAccessDAO.daoContainsAuthToken("12345"));
    }

    @Test
    public void deleteAuthTokenNegative() {
        dataAccessDAO.daoStoreAuthToken(new AuthData("12345", "Tom"));
        dataAccessDAO.daoStoreAuthToken(new AuthData("56789", "Irene"));
        dataAccessDAO.daoDeleteAuthToken("12345");
        assertTrue(dataAccessDAO.daoContainsAuthToken("56789"));
    }

    @AfterEach
    public void cleanTheSlate() {
        dataAccessDAO.nukeEverything();
    }


    /*
    protected abstract ArrayList<GameData> daoGetGames();
    protected abstract int daoAddGame(String gameName);
    protected abstract boolean daoIsTeamColorFree(int gameID, String color);
    protected abstract void daoJoinGame(int gameID, String color, String username);
    protected abstract boolean daoIsGameNumberValid(int gameID);

 */
}
