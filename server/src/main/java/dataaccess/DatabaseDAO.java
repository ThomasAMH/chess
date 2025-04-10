package dataaccess;

import chess.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dbobjects.GameRecord;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class DatabaseDAO extends DataAccessDAO {
    GsonBuilder chessGameBuilder;
    public DatabaseDAO() throws DataAccessException{
        DatabaseManager.createDatabase();
        DatabaseManager.initializeDatabase();
        chessGameBuilder = new GsonBuilder();
        chessGameBuilder.registerTypeAdapter(ChessGame.class, new ChessGameTypeAdapter());
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
                    if(rs.next()) {
                        return true;
                    };
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
    @Override
    protected String daoGetUsernameFromAuthToken(String authToken) {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username FROM authdata WHERE authtoken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if(rs.next()) {
                        return rs.getNString("username");
                    } else {
                        return "";
                    }
                }
            }
        } catch (Exception e) {
            return "";
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
        ArrayList<GameData> gameData = new ArrayList<GameData>();
        String whiteUsername, blackUsername, gameName, gameString;
        int gameID;
        ChessGame chessGame;
        GameData gameDataEntry;
//        chessGameBuilder = new GsonBuilder();
//        chessGameBuilder.registerTypeAdapter(ChessGame.class, new ChessGameTypeAdapter());
        Gson gson = chessGameBuilder.create();

        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM gamedata";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while(rs.next()) {
                        whiteUsername = rs.getNString("white_username");
                        blackUsername = rs.getNString("black_username");
                        gameName = rs.getNString("game_name");
                        gameID = rs.getInt("game_id");
                        gameString = rs.getNString("game");
                        chessGame = gson.fromJson(gameString, ChessGame.class);
                        gameDataEntry = new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
                        gameData.add(gameDataEntry);
                    }
                    return gameData;
                }
            }  catch (Exception e) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected int daoAddGame(String gameName) {
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "INSERT INTO gamedata (white_username, black_username, game_name, game) VALUES (?, ?, ?, ?)";
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                String json = new Gson().toJson(new ChessGame());
                ps.setString(1, null);
                ps.setString(2, null);
                ps.setString(3, gameName);
                ps.setString(4, json);
                ps.executeUpdate();
                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;

            }
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    protected boolean daoIsTeamColorFree(int gameID, String color) {
        String whiteUsername, blackUsername;
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT white_username, black_username FROM gamedata WHERE game_id=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if(!rs.next()) {
                        return false;
                    }
                    whiteUsername = rs.getNString("white_username");
                    blackUsername = rs.getNString("black_username");
                    if(color.equals("WHITE")) {
                        return whiteUsername == null;
                    } else if(color.equals("BLACK")) {
                        return blackUsername == null;
                    } else {
                        return false;
                    }
                }
            }  catch (Exception e) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    protected void daoJoinGame(int gameID, String color, String authToken) {
        String whiteUsername, blackUsername;
        String username = daoGetUsernameFromAuthToken(authToken);
        String statement = null;
        if(color.equals("WHITE")) {
            statement = "UPDATE gamedata SET white_username = ? WHERE game_id=?";
        } else {
            statement = "UPDATE gamedata SET black_username = ? WHERE game_id=?";
        }
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                ps.setInt(2, gameID);
                ps.executeUpdate();
            }  catch (Exception e) {
                return;
            }
        } catch (Exception e) {
            return;
        }
    }

    @Override
    protected boolean daoIsGameNumberValid(int gameID) {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT game_id FROM gamedata WHERE game_id=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    protected String daoGetPlayerUsername(int gameID, ChessGame.TeamColor color) {
        String statement;
        String col_name;
        try (var conn = DatabaseManager.getConnection()) {
            if(color == ChessGame.TeamColor.WHITE) {
                col_name = "white_username";
                statement = "SELECT " +  col_name + " FROM gamedata WHERE game_id=?";
            } else {
                col_name = "black_username";
                statement = "SELECT " +  col_name + " FROM gamedata WHERE game_id=?";
            }
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    rs.next();
                    return rs.getNString(col_name);
                }
            }
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    protected GameRecord daoGetGameByID(Integer gameID) {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT game_id, white_username, black_username, game_name, game FROM gamedata WHERE game_id=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    rs.next();
                    String whiteUsername = rs.getNString("white_username");
                    String blackUsername = rs.getNString("black_username");
                    String gameName = rs.getNString("game_name");
                    String gameJSON = rs.getNString("game");
                    return new GameRecord(gameJSON, whiteUsername, blackUsername, gameName, gameID);
                }
            } catch (Exception e) {
                return null;
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void daoUpdateGameByID(Integer gameID, String gameJson) {
        String statement = "UPDATE gamedata SET game = ? WHERE game_id=?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, gameJson);
                ps.setInt(2, gameID);
                ps.executeUpdate();
            }  catch (Exception e) {
                return;
            }
        } catch (Exception e) {
            return;
        }
    }

    @Override
    protected void daoDeleteGameByID(Integer gameID) {
        String statement = statement = "DELETE FROM gamedata WHERE game_id=?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, gameID.toString());
                ps.executeUpdate();
            }  catch (Exception e) {
                return;
            }
        } catch (Exception e) {
            return;
        }
    }

    @Override
    protected void daoRemoveUserFromGameByID(Integer gameID, String userName) {
        String statement = "UPDATE gamedata SET white_username = null WHERE game_id = ? AND white_username = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, gameID.toString());
                ps.setString(2, userName);
                ps.executeUpdate();
            }  catch (Exception e) {
                return;
            }
        } catch (Exception e) {
            return;
        }
        statement = "UPDATE gamedata SET black_username = null WHERE game_id = ? AND white_username = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, gameID.toString());
                ps.setString(2, userName);
                ps.executeUpdate();
            }  catch (Exception e) {
                return;
            }
        } catch (Exception e) {
            return;
        }
    }

    @Override
    public void nukeEverything() {
        String[] nukeCodes = {"DROP TABLE gamedata", "DROP TABLE userdata", "DROP TABLE authdata","DROP DATABASE chess"};
        try (var conn = DatabaseManager.getConnection()) {
            for(String nukeCode: nukeCodes) {
                try (var ps = conn.prepareStatement(nukeCode)) {
                        ps.executeUpdate();
                    } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            } catch (Exception e) {
                return;
        }
    }

    public void clearDatabase() {
        String[] statements = {"DELETE FROM gamedata", "DELETE FROM userdata", "DELETE FROM authdata"};
        try (var conn = DatabaseManager.getConnection()) {
            for(String statement: statements) {
                try (var ps = conn.prepareStatement(statement)) {
                    ps.executeUpdate();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        } catch (Exception e) {
            return;
        }
    }


}
