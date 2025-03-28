package service;

import dataaccess.DataAccessDAO;
import dataaccess.DataAccessException;
import requests.LogoutRequest;
import results.DataAccessResult;

import results.LogoutResult;

public class LogoutService {
    public LogoutResult logoutUser(LogoutRequest request, DataAccessDAO dataService) {
        //Check of the token provided is active
        try {
            DataAccessResult daoResult = dataService.authData.doesAuthTokenExist(request.authToken());
            if (daoResult.data().equals("false")) {
                return new LogoutResult(401, "Error: unauthorized");
            }
        } catch (DataAccessException e) {
            return new LogoutResult(500, "Error: Unknown error detected in token query process.");
        }
        //Delete it
        try {
            DataAccessResult result = dataService.authData.deleteAuthToken(request.authToken());
            return new LogoutResult(200, result.data());
        } catch (DataAccessException e) {
            return new LogoutResult(500, "Error: unexpected error in game fetching process");
        }
    }
}
