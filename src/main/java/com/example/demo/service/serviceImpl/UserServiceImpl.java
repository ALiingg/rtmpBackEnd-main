package com.example.demo.service.serviceImpl;

import com.example.demo.domain.User;
import com.example.demo.repository.UserDao;
import com.example.demo.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    // Inject the UserDao dependency to interact with the user database
    @Resource
    private UserDao userDao;

    /**
     * Service method to handle user login.
     * @param uname Username of the user trying to log in
     * @param password Password of the user
     * @return The User object( if authentication is successful, otherwise null with an empty password field
     */
    @Override
    public User loginService(String uname, String password) {
        // Fetch the user by username and password
        User user = userDao.findByUnameAndPassword(uname, password);

        // If the user is not found, initialize an empty password for security
        if (user == null) {
            user.setPassword("");
        }

        return user; // Return the user object (or null if not found)
    }
    /**
     * Service method to handle user login by email.
     * @param email Username of the user trying to log in
     * @param password Password of the user
     * @return The User object if authentication is successful, otherwise null with an empty password field
     */
    @Override
    public User loginByEmail(String email, String password){
        User user = userDao.findByEmailAndPassword(email, password);
        if (user == null) {
            user.setPassword("");
        }
        return user;
    }
    /**
     * Service method to handle user registration.
     * Checks if a username already exists; if not, registers a new user.
     * @param user User object containing registration details
     * @return The newly created User object if registration is successful, otherwise null
     */
    @Override
    public User registService(User user) {
        // Check if a user with the same username already exists
        if (userDao.findByUname(user.getUsername()) != null) {
            return null; // Return null if username is already taken
        } else {
            // Save the new user in the database
            User newUser = userDao.save(user);

            // Optionally clear the password field for security after saving
            // newUser.setPassword("");

            return newUser; // Return the newly created user
        }
    }

    /**
     * Service method to update a user's token in the database.
     * @param user User object containing the new token
     * @return The updated User object
     */
    @Override
    public User updateToken(User user) {
        // Save the updated user object with the new token
        User newUser = userDao.save(user);
        return newUser; // Return the updated user
    }

    /**
     * Service method to authenticate a user by token.
     * @param token Authentication token of the user
     * @return The User object if the token is valid, otherwise null
     */
    public User tokenService(String token) {
        // Find the user by token
        User user = userDao.findByToken(token);
        return user; // Return the user object (or null if not found)
    }
}
