package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;

public class DatabaseDAO extends DataAccessDAO {

    public DatabaseDAO() throws DataAccessException{
        DatabaseManager.createDatabase();
        DatabaseManager.initializeDatabase();
    }

    @Override
    protected boolean daoDoesUserExist(String username) {
        return false;
    }

    @Override
    protected UserData daoGetUserData(String username) {
        return null;
    }

    @Override
    protected void daoSaveNewUser(UserData userData) {

    }

    @Override
    protected void daoStoreAuthToken(AuthData data) {

    }

    @Override
    protected boolean daoContainsAuthToken(String username) {
        return false;
    }

    @Override
    protected void daoDeleteAuthToken(String username) {

    }

    @Override
    protected ArrayList<GameData> daoGetGames() {
        return null;
    }

    @Override
    protected int daoAddGame(String gameName) {
        return 0;
    }

    @Override
    protected int daoGetChessGameIndex() {
        return 0;
    }

    @Override
    protected boolean daoIsTeamColorFree(int gameID, String color) {
        return false;
    }

    @Override
    protected void daoJoinGame(int gameID, String color, String username) {

    }

    @Override
    protected boolean daoIsGameNumberValid(int gameID) {
        return false;
    }

    @Override
    public void nukeEverything() {

    }
}
