package chess;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ChessGameTypeAdapter extends TypeAdapter<ChessGame> {
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

        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            switch (name) {
                case "gameBoard" -> gameBoard = new Gson().fromJson(jsonReader, ChessBoard.class);
                case "gameState" -> gameState = determineGameState(jsonReader.nextString());
                case "activePlayer" -> activePlayer = determineTeamColor(jsonReader.nextString());
                case "whitePieces" -> whitePieces = readPieceMaps(new Gson().fromJson(jsonReader, HashMap.class));
                case "blackPieces" -> blackPieces = readPieceMaps(new Gson().fromJson(jsonReader, HashMap.class));
            }
        }
        jsonReader.endObject();
        return new ChessGame(gameBoard, activePlayer, blackPieces, whitePieces, gameState);
    }

    private ChessGame.GameState determineGameState(String gameState) {
        switch (gameState) {
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
        if (teamColor.equals("WHITE")) {
            return ChessGame.TeamColor.WHITE;
        } else {
            return ChessGame.TeamColor.BLACK;
        }
    }

    private HashMap<ChessPosition, Collection<ChessMove>> readPieceMaps(HashMap<String,
            ArrayList<LinkedTreeMap<String, LinkedTreeMap<String, Double>>>> stringMap) {
        HashMap<ChessPosition, Collection<ChessMove>> returnObj = new HashMap<ChessPosition, Collection<ChessMove>>();
        for (Map.Entry<String, ArrayList<LinkedTreeMap<String, LinkedTreeMap<String, Double>>>> entry : stringMap.entrySet()) {
            ChessPosition key = ChessPosition.fromString(entry.getKey()); // Assuming fromString("e2") etc.
            ArrayList<ChessMove> validMoves = new ArrayList<ChessMove>();
            ArrayList<LinkedTreeMap<String, LinkedTreeMap<String, Double>>> moves = entry.getValue();
            for (LinkedTreeMap<String, LinkedTreeMap<String, Double>> position : moves) {
                LinkedTreeMap<String, Double> endPos = position.get("endPosition");
                LinkedTreeMap<String, Double> startPos = position.get("startPosition");
                int startRow, startCol, endRow, endCol;
                startRow = startPos.get("row").intValue();
                endRow = endPos.get("row").intValue();

                startCol = startPos.get("col").intValue();
                endCol = endPos.get("col").intValue();
                ChessPosition startPosition = new ChessPosition(startRow, startCol);
                ChessPosition endPosition = new ChessPosition(endRow, endCol);
                validMoves.add(new ChessMove(startPosition, endPosition, null));
            }
            returnObj.put(key, validMoves);
        }
        return returnObj;
    }
}