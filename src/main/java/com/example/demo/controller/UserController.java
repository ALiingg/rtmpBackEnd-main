package com.example.demo.controller;

import com.example.demo.domain.User;
import com.example.demo.domain.VerificationCode;
import com.example.demo.repository.VerificationCodeRepository;
import com.example.demo.service.EmailService;
import com.example.demo.service.UserService;
import com.example.demo.service.serviceImpl.UserServiceImpl;
import com.example.demo.utils.CodeGenerator;
import com.example.demo.utils.Result;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.demo.utils.randomUUID;

import java.time.LocalDateTime;
import java.util.Objects;

@RestController
@RequestMapping("/user")
public class UserController {

    // Inject UserService to handle user-related operations
    @Resource
    private UserService userService;

    // Inject UserServiceImpl for direct access to specific implementation details
    @Autowired
    private UserServiceImpl userServiceImpl;

        @Autowired
        private EmailService emailService;

        @Autowired
        private VerificationCodeRepository verificationCodeRepository;

        @PostMapping("/sendVerificationCode")
        public Result<String> sendVerificationCode(@RequestParam String email) {
            String code = CodeGenerator.generateCode(); // Generate OTP


            VerificationCode verificationCode = new VerificationCode();
            verificationCode.setEmail(email);
            verificationCode.setCode(code);
            verificationCode.setCreatedAt(LocalDateTime.now());
            verificationCode.setExpiresAt(LocalDateTime.now().plusMinutes(5));

            verificationCodeRepository.save(verificationCode); // save to db
            emailService.sendEmail(email, code); // send email
            return Result.success("456", "Verification Code Sent");
        }

            @Transactional

        public boolean verifyCode(String email, String code) {
            VerificationCode verificationCode = verificationCodeRepository
                    .findByEmailAndCode(email, code)
                    .orElse(null);

            if (verificationCode != null && verificationCode.getExpiresAt().isAfter(LocalDateTime.now())) {
                verificationCodeRepository.deleteByEmail(email); // Delete Code after verify
                return true;
            } else {
                return false;
            }
        }


    @PostMapping("/loginByEmail")
    public Result<User> loginByEmail(@RequestParam String email, @RequestParam String password) {
        User user = userServiceImpl.loginByEmail(email, password);
        if (user != null) {
            String token = randomUUID.getUUID();
            user.setToken(token);

            // Update the token in thea database
            userService.updateToken(user);
            return Result.success(user, "Login successful!");
        } else {
            // Return error if authentication fails
            return Result.error("123", "Incorrect username or password!");

        }
    }
    /**
     * Handles user login requests.
     * @param uname Username of the user trying to log in
     * @param password Password of the user
     * @return Result object containing user data and success message if login is successful,
     * or error message if login fails
     */
    @PostMapping("/login")
    public Result<User> loginController(@RequestParam String uname, @RequestParam String password) {
        // Call the login service to authenticate user

        User user = userService.loginService(uname, password);

        // Check if user exists
        if (user != null) {
            // Generate a unique token and assign it to the user
            String token = randomUUID.getUUID();
            user.setToken(token);

            // Update the token in the database
            userService.updateToken(user);
            System.out.println(token); // Print the token to console for debugging

            // Return success result with user information
            return Result.success(user, "Login successful!");
        } else {
            // Return error if authentication fails
            return Result.error("123", "Incorrect username or password!");
        }
    }

    /**
     * Handles user registration requests.
     * @param newUser New user object containing registration details
     * @return Result object with success message if registration is successful,
     * or error message if the username already exists
     */
    @Transactional
    @PostMapping("/register")
    public Result<User> registController(@RequestParam String uname, @RequestParam String password, @RequestParam String passcode, @RequestParam String email, @RequestParam String code) {
        if (!verifyCode(email, code)) {
            System.out.println(code);
            System.out.println(email);
            return Result.error("123", "Incorrect verification code!");
        }
        String pscode = "123456";
        System.out.println(passcode + passcode.equals(pscode));
        if (!passcode.equals(pscode)) {
            return Result.error("123", "Incorrect passcode!");
        }
        User newUser = new User();
        newUser.setUsername(uname);
        newUser.setPassword(password);
        newUser.setEmail(email);


        // Call the registration service to create a new user
        User user = userService.registService(newUser);

        // Check if the user was successfully created
        if (user != null) {
            // Return success result with the new user's information
            return Result.success(user, "Registration successful!");
        } else {
            // Return error if username already exists
            return Result.error("999", "Username already exists!");
        }
    }

    /**
     * Handles token-based login requests.
     * @param token Unique token provided by the user
     * @return Result object containing user data and success message if token is valid,
     * or error message if token is invalid
     */
    @PostMapping("/tokenlogin")
    public Result<User> tokenLoginController(@RequestBody String token) {
        // Use the token to retrieve the user via the token service
        User user = userServiceImpl.tokenService(token);
        System.out.println(token); // Print the token to console for debugging

        // Check if a valid user was found
        if (user != null) {
            // Return success result with user information
            return Result.success(user, "Token valid");
        } else {
            // Return error if the token is invalid
            return Result.error("456", "Token invalid");
        }
    }
}
