package service;

import requests.LoginRequest;
import results.LoginResult;

public class LoginService {
    LoginResult loginUser(LoginRequest request) {
        LoginResult result = new LoginResult(200, "test","test","12345");
        //getUsernameValidity
        //getAuthToken
        return result;
    }
}
