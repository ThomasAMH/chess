package service;

import dataaccess.DataAccessDAO;
import dataaccess.DataAccessException;
import requests.LoginRequest;
import results.DataAccessResult;
import results.LoginResult;
import results.RegisterResult;

import java.util.Objects;

public class LoginService {
    public LoginResult loginUser(LoginRequest request, DataAccessDAO dataService) {
        LoginResult result = new LoginResult(200, "test","test","12345");

        //Is username valid?
        try {
            DataAccessResult daoResult = dataService.userData.doesUserExist(request.username());
            if(daoResult.data().equals("false")) {
                return new LoginResult(401, "Error: unauthorized", request.username(), "");
            }
        } catch (DataAccessException e) {
            return new LoginResult(500, e.getMessage(), request.username(), "");
        }

        //Is password correct?
        try {
            String password = dataService.userData.getPassword(request.username()).data();
            if(Objects.equals(password, request.password())) {
                //Get auth token
                String token = dataService.authData.getAuthToken(request.username()).data();
                return new LoginResult(200,"Login successful", request.username(), token);
            } else {
                return new LoginResult(401,"Error: unauthorized", request.username(), "");
            }

        } catch (DataAccessException e) {
            return new LoginResult(500, e.getMessage(), request.username(), "");
        }
    }
}
