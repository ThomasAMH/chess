package requests;

public record JoinGameRequest(String playerColor, int gameID, String username, String authToken) {

}
