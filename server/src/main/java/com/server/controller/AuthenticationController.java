package com.server.controller;

import com.server.dao.UserDao;
import com.server.exception.DaoException;
import com.server.model.LoginDto;
import com.server.model.LoginResponseDto;
import com.server.model.RegisterUserDto;
import com.server.model.User;
import com.server.security.jwt.TokenProvider;
import com.server.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * AuthenticationController is a class used for handling requests to authenticate Users.
 *
 * It depends on an instance of a UserDao for retrieving and storing user data. This is provided
 * through dependency injection.
 */

@RequestMapping("/api")
@RestController
@CrossOrigin
public class AuthenticationController {

    private final TokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private UserDao userDao;
    private EmailService emailService;

    public AuthenticationController(TokenProvider tokenProvider, AuthenticationManager authenticationManager, UserDao userDao, EmailService emailService) {
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
        this.userDao = userDao;
        this.emailService = emailService;
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public LoginResponseDto login(@Valid @RequestBody LoginDto loginDto) {
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());

            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            if(authentication.isAuthenticated()){
                String jwt = tokenProvider.createToken(authentication, false);
                User user = userDao.getUserByEmail(loginDto.getEmail());
                return new LoginResponseDto(jwt, user);
            }

            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED); //Status Code: 401 = The user isn't allowed to perform this action
        }
        catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "DAO error - " + e.getMessage()); //Status Code: 500 = API itself has a problem and can't fulfill the request at this time
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public User register(@Valid @RequestBody RegisterUserDto newUser) {


        if(!newUser.isPasswordsMatch()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "password and confirm password do not match"); //Status Code: 400 = Data validation problem
        }

        // Normalize email to lowercase
        String lowerCaseEmail = newUser.getEmail().toLowerCase();


        // Check if user already exists
        if(userDao.getUserByEmail(lowerCaseEmail) != null) {

            throw new ResponseStatusException(HttpStatus.CONFLICT, "An account with this email already exists.");
        }


        try {
            User user = userDao.createUser(new User(newUser.getName(), newUser.getDisplayName(), lowerCaseEmail, newUser.getPassword(), newUser.getRole()));

            
            // Send welcome email asynchronously (don't block registration response)
            try {
                emailService.sendWelcomeEmail(user.getEmail(), user.getName());
            } catch (Exception emailException) {
                // Log but don't fail registration if email fails
                System.err.println("Failed to send welcome email: " + emailException.getMessage());
            }
            
            return user;
        }
        catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "DAO error - " + e.getMessage()); //Status Code: 500 = API itself has a problem and can't fulfill the request at this time
        }
    }

}
