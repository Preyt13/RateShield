package com.RateShield.auth;

public interface UserRepository {
    boolean registerUser(User user);
    User validateUser(String username, String password);
    User getUser(String username);
}