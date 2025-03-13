package dataaccess;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
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
        ArrayList<GameData> gameData = new ArrayList<GameData>();
        String whiteUsername, blackUsername, gameName, gameString;
        int gameID;
        ChessGame chessGame;
        GameData gameDataEntry;
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
                ps.setString(1, "");
                ps.setString(2, "");
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
                    if(rs.next()) {
                        whiteUsername = rs.getNString("white_username");
                        blackUsername = rs.getNString("black_username");
                        if(color.equals("WHITE")) {
                            return whiteUsername.isEmpty();
                        } else if(color.equals("BLACK")) {
                            return blackUsername.isEmpty();
                        } else {
                            return false;
                        }
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
    protected void daoJoinGame(int gameID, String color, String username) {
        String whiteUsername, blackUsername;
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

    private static class ChessGameTypeAdapter extends TypeAdapter<ChessGame> {
        @Override
        public void write(JsonWriter jsonWriter, ChessGame chessGame) throws IOException {
            Gson gson = new Gson();
            gson.getAdapter(ChessGame.class).write(jsonWriter, chessGame);
        }

        @Override
        public ChessGame read(JsonReader jsonReader) throws IOException {
            ChessBoard gameBoard = null;
            ChessGame.TeamColor activePlayer = null;
            HashMap<ChessPosition, Collection<ChessMove>> blackPieces = null;
            HashMap<ChessPosition, Collection<ChessMove>> whitePieces = null;
            ChessGame.GameState gameState = null;
            jsonReader.beginObject();

            while(jsonReader.hasNext()) {
                String name = jsonReader.nextName();
                switch(name) {
                    case "gameBoard" -> gameBoard = new Gson().fromJson(jsonReader, ChessBoard.class);
                    case "gameState" -> gameState = determineGameState(jsonReader.nextString());
                    case "activePlayer" -> activePlayer = determineTeamColor(jsonReader.nextString());
                    case "whitePieces" -> whitePieces = new Gson().fromJson(jsonReader, HashMap.class);
                    case "blackPieces" -> blackPieces = new Gson().fromJson(jsonReader, HashMap.class);
                }
            }
            jsonReader.endObject();
            return new ChessGame(gameBoard, activePlayer, blackPieces, whitePieces, gameState);
        }

        private ChessGame.GameState determineGameState(String gameState) {
            switch(gameState) {
                case "NORMAL" -> {
                    return ChessGame.GameState.NORMAL;
                }
                case "CHECK" -> {
                    return ChessGame.GameState.CHECK;
                }
                case "STALEMATE" -> {
                    return ChessGame.GameState.STALEMATE;
                }
                case "CHECKMATE" -> {
                    return ChessGame.GameState.CHECKMATE;
                }
                default -> {
                    return null;
                }
            }
        }
        private ChessGame.TeamColor determineTeamColor(String teamColor) {
            if(teamColor.equals("WHITE")) {
                return ChessGame.TeamColor.WHITE;
            } else {
                return ChessGame.TeamColor.BLACK;
            }
        }
        private HashMap<ChessPosition, Collection<ChessMove>> createTeamPieces(ChessGame.TeamColor teamColor, String pieces) {
            HashMap<ChessPosition, Collection<ChessMove>> teamMoves = new HashMap<ChessPosition, Collection<ChessMove>>();

            return teamMoves;
        }
    }

}
