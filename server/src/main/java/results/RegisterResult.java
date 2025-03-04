package results;

public record RegisterResult(int responseCode, String responseMessage, String username, String authToken) {
}
