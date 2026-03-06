package service;

import dao.IUserDao;
import exception.AuthException;
import model.userModel;
import util.PasswordUtil;

public class AuthService {

    private final IUserDao userDao;

    public AuthService(IUserDao userDao) {
        this.userDao = userDao;
    }

    public userModel authenticate(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new AuthException("Username and password are required");
        }

        userModel user = userDao.findByUsername(username.trim());
        if (user == null) throw new AuthException("Invalid credentials");

        if (!PasswordUtil.matches(password, user.getPasswordHash())) {
            throw new AuthException("Invalid credentials");
        }

        return user;
    }
}