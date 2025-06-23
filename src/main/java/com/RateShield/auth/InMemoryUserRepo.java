package com.RateShield.auth;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class InMemoryUserRepo implements UserRepository {
    private final Map<String, User> userStore = new ConcurrentHashMap<>();

    @Override
    public boolean registerUser(User user) {
        if (userStore.containsKey(user.getUsername())) return false;
        userStore.put(user.getUsername(), user);
        return true;
    }

    @Override
    public User validateUser(String username, String password) {
        User user = userStore.get(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    @Override
    public User getUser(String username) {
        return userStore.get(username);
    }
}
