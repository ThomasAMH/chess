package dataaccess;

import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestGameDaoFunctions {
    DataAccessDAO dataAccessDAO;

    public TestGameDaoFunctions() {
        try {
            dataAccessDAO = new DatabaseDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void addGamePositive() {
        int test1 = dataAccessDAO.daoAddGame("Mitch");
        int test2 = dataAccessDAO.daoAddGame("Arnold");

        assertTrue(dataAccessDAO.daoIsGameNumberValid(test1));
        assertTrue(dataAccessDAO.daoIsGameNumberValid(test2));

    }
    @Test
    public void addGameNegative() {
        int test1 = dataAccessDAO.daoAddGame("Mitch");
        int test2 = dataAccessDAO.daoAddGame("Arnold");

        assertFalse(dataAccessDAO.daoIsGameNumberValid(100));
        assertFalse(dataAccessDAO.daoIsGameNumberValid(-5));
    }

    @Test
    public void getGamesPositive() {
        int test1 = dataAccessDAO.daoAddGame("Mitch");
        int test2 = dataAccessDAO.daoAddGame("Arnold");

        assertEquals(2, dataAccessDAO.daoGetGames().size());
    }

    @Test
    public void getGamesNegative() {
        int test1 = dataAccessDAO.daoAddGame("Mitch");
        int test2 = dataAccessDAO.daoAddGame("Arnold");

        assertNotEquals(3, dataAccessDAO.daoGetGames().size());
    }

    @Test
    public void joinGamePositive() {
        int test1 = dataAccessDAO.daoAddGame("Mitch");
        dataAccessDAO.daoJoinGame(1, "WHITE", "Tom");
        assertFalse(dataAccessDAO.daoIsTeamColorFree(test1, "WHITE"));
    }

    @Test
    public void joinGameNegative() {
        //Validation of join is handled by the server and the handler...
        int test1 = dataAccessDAO.daoAddGame("Mitch");
        dataAccessDAO.daoJoinGame(2, "WHITE", "Tom");
        assertTrue(dataAccessDAO.daoIsTeamColorFree(test1, "WHITE"));
    }

    @AfterEach
    public void cleanTheSlate() {
        dataAccessDAO.nukeEverything();
    }
}
