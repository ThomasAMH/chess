package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class DatabaseDAO extends DataAccessDAO {

    public DatabaseDAO() throws DataAccessException{
        DatabaseManager.createDatabase();
        DatabaseManager.initializeDatabase();
    }

    @Override
    protected boolean daoDoesUserExist(String username) {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username FROM userdata WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    protected UserData daoGetUserData(String username) {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM userdata WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if(rs.next()) {
                        UserData returnData = new UserData(rs.getNString("username"),rs.getNString("password"),rs.getNString("email"));
                        return returnData;
                    } else {
                        return null;
                    }
                }
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected boolean daoSaveNewUser(UserData userData) {
        try (var conn = DatabaseManager.getConnection()) {
            String username = userData.username();
            String password = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
            String email = userData.email();
            String statement = "INSERT INTO userdata (username, password, email) VALUES (?, ?, ?)";
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                ps.setString(1, username);
                ps.setString(2, password);
                ps.setString(3, email);

                ps.executeUpdate();
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    protected boolean daoIsPasswordValid(UserData userData) {
        String proposedPassword = userData.password();
        String storedPassword = daoGetUserData(userData.username()).password();
        return BCrypt.checkpw(proposedPassword, storedPassword);
    }

    @Override
    protected boolean daoStoreAuthToken(AuthData data) {
        try (var conn = DatabaseManager.getConnection()) {
            String username = data.username();
            String token = data.authToken();
            String statement = "INSERT INTO authdata (authtoken, username) VALUES (?, ?)";
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                ps.setString(1, token);
                ps.setString(2, username);
                ps.executeUpdate();
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    protected boolean daoContainsAuthToken(String token) {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authtoken FROM authdata WHERE authtoken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, token);
                try (var rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    protected void daoDeleteAuthToken(String token) {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "DELETE FROM authdata WHERE (authtoken = ?)";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, token);
                ps.executeUpdate();
                return;
            }
        } catch (Exception e) {
            return;
        }
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
