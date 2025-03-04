package service;

import dataaccess.DataAccessDAO;
import dataaccess.DataAccessException;
import requests.RegisterRequest;
import results.DataAccessResult;
import results.RegisterResult;

public class RegistrationService {

    public RegisterResult registerUser(RegisterRequest request, DataAccessDAO dataService) {
        //Validate username
        try {
            DataAccessResult daoResult = dataService.userData.doesUserExist(request.username());
            if(daoResult.data().equals("true")) {
                return new RegisterResult(403, "Error: already taken", request.username(), "");
            }
        } catch (DataAccessException e) {
            return new RegisterResult(500, e.getMessage(), request.username(), "");
        }
        //Attempt to add user data
        try {
            DataAccessResult daoResult = dataService.userData.createUser(request);
        } catch (DataAccessException e) {
            return new RegisterResult(400, e.getMessage(), request.username(), "");
        }
        //getAuthToken
        try {
            String token = dataService.authData.getAuthToken(request.username()).data();
            return new RegisterResult(200, "User saved", request.username(), token);
        } catch (DataAccessException e) {
            return new RegisterResult(500, e.getMessage(), request.username(), "");
        }
    }
}
