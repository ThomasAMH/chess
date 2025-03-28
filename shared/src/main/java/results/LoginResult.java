package results;

public record LoginResult(int responseCode, String responseMessage, String username, String authToken) {
}
