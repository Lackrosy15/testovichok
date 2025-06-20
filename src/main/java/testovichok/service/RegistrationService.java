package testovichok.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import testovichok.dao.UserDao;
import testovichok.entityes.ChangePasswordCredentials;
import testovichok.entityes.RegistrationCredentials;
import testovichok.entityes.Roles;
import testovichok.entityes.User;
import testovichok.exceptions.IncorrectDataFormatException;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class RegistrationService {

    private final UserDao userDao;
    private static final Pattern emailPattern = Pattern.compile("^[A-Z0-9a-z+_.-]+@[A-Za-z0-9._-]+$");
    private static final Pattern passwordPattern = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d).{8,}$");

    @SneakyThrows
    public boolean isValidateData(String email, String password) {
        Matcher emailMatcher = emailPattern.matcher(email);
        Matcher passwordMatcher = passwordPattern.matcher(password);

        if (emailMatcher.matches()) {
            if (passwordMatcher.matches()) {
                return true;
            } else {
                throw new IncorrectDataFormatException();
            }
        } else {
            throw new IncorrectDataFormatException();
        }
    }

    public void registerUser(RegistrationCredentials registrationCredentials) {
        User user = new User(UUID.randomUUID(), registrationCredentials.getName(), registrationCredentials.getLogin(), SecurityService.hashPassword(registrationCredentials.getPassword()), Roles.USER);
        userDao.addUser(user);
    }

    public void changePassword(ChangePasswordCredentials changePasswordCredentials, HttpServletRequest req) {
        User user = (User) req.getSession().getAttribute("user");
        boolean isCorrectPassword = SecurityService.passwordEncoder.matches(changePasswordCredentials.getCurrentPassword(), user.getPassword());

        if (isCorrectPassword) {
            Matcher passwordMatcher = passwordPattern.matcher(changePasswordCredentials.getNewPassword());
            if (passwordMatcher.matches()) {
                user.setPassword(SecurityService.hashPassword(changePasswordCredentials.getNewPassword()));
                userDao.updateUser(user);
            }
        }
    }
}
