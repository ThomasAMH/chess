package dataaccess;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.Properties;
import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class DatabaseManager {
    private static final String DATABASE_NAME;
    private static final String USER;
    private static final String PASSWORD;
    private static final String CONNECTION_URL;
    private static final String[] CREATE_DB_STRINGS =
            {"""
            CREATE TABLE IF NOT EXISTS dbname.gamedata (
              `game_id` INT NOT NULL AUTO_INCREMENT,
              `white_username` VARCHAR(60) NULL DEFAULT NULL,
              `black_username` VARCHAR(60) NULL DEFAULT NULL,
              `game_name` VARCHAR(60) NULL DEFAULT NULL,
              `game` JSON NULL DEFAULT NULL,
              PRIMARY KEY (`game_id`),
              UNIQUE INDEX `gameid_UNIQUE` (`game_id` ASC) VISIBLE)
            ENGINE = InnoDB
            DEFAULT CHARACTER SET = utf8mb4
            COLLATE = utf8mb4_0900_ai_ci;
            """
            ,
            """
            CREATE TABLE IF NOT EXISTS dbname.userdata (
              `username` VARCHAR(60) NOT NULL,
              `password` VARCHAR(60) NULL DEFAULT NULL,
              `email` VARCHAR(60) NULL DEFAULT NULL,
              PRIMARY KEY (`username`),
              UNIQUE INDEX `username_UNIQUE` (`username` ASC) VISIBLE)
            ENGINE = InnoDB
            DEFAULT CHARACTER SET = utf8mb4
            COLLATE = utf8mb4_0900_ai_ci;
            """,
            """
            CREATE TABLE IF NOT EXISTS dbname.authdata (
              `authtoken` VARCHAR(60) NOT NULL,
              `username` VARCHAR(60) NULL DEFAULT NULL,
              PRIMARY KEY (`authtoken`))
            ENGINE = InnoDB
            DEFAULT CHARACTER SET = utf8mb4
            COLLATE = utf8mb4_0900_ai_ci;
            """
    };

    /*
     * Load the database information for the db.properties and db_create_string.txt files.
     */
    static {
        try {
            try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
                if (propStream == null) {
                    throw new Exception("Unable to load db.properties");
                }
                Properties props = new Properties();
                props.load(propStream);
                DATABASE_NAME = props.getProperty("db.name");
                USER = props.getProperty("db.user");
                PASSWORD = props.getProperty("db.password");

                var host = props.getProperty("db.host");
                var port = Integer.parseInt(props.getProperty("db.port"));
                CONNECTION_URL = String.format("jdbc:mysql://%s:%d", host, port);
            }
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties. " + ex.getMessage());
        }

    }

    /**
     * Creates the database if it does not already exist.
     */

    static void createDatabase() throws DataAccessException {
        try (var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD)) {
            try (var preparedStatement = conn.prepareStatement("CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME, RETURN_GENERATED_KEYS)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    static void initializeDatabase() throws DataAccessException {
        String cleanStatement;
        try (var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD)) {
            for (String statement : CREATE_DB_STRINGS) {
                cleanStatement = statement.replaceFirst("dbname.", DATABASE_NAME+".");
                try (var preparedStatement = conn.prepareStatement(cleanStatement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

        /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DbInfo.getConnection(databaseName)) {
     * // execute SQL statements.
     * }
     * </code>
     */

    static Connection getConnection() throws DataAccessException {
        try {
            var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
            conn.setCatalog(DATABASE_NAME);
            return conn;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}