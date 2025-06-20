package testovichok.service;

import testovichok.entityes.LoginAttempt;
import testovichok.entityes.LoginCredentials;
import testovichok.exceptions.UserBlockedException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class LoginAttemptService {
    private final Map<String, LoginAttempt> loginAttempts = new ConcurrentHashMap<>();

    public LoginAttempt checkUserAccess(LoginCredentials loginCredentials) {
        loginAttempts.putIfAbsent(loginCredentials.getLogin(), new LoginAttempt());
        LoginAttempt loginAttempt = loginAttempts.get(loginCredentials.getLogin());

        if (loginAttempt.isBlockedExpired()) {
            loginAttempt.setCountOfAttempts(new AtomicInteger(0));
        }

        if (loginAttempt.isBlocked()) {
            throw new UserBlockedException();
        }
        return loginAttempt;
    }
}
