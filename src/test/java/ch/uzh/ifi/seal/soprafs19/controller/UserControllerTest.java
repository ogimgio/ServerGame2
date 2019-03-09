package ch.uzh.ifi.seal.soprafs19.controller;

import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;
import ch.uzh.ifi.seal.soprafs19.service.UserService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import static org.junit.Assert.*;

public class UserControllerTest {

    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserController userController;

    @Test
    public void all() {

    }

    @Test
    public void getUser() {
    }

    @Test
    public void deleteUser() {

    }

    @Test
    public void changeUser() {

    }

    @Test
    public void createUser() {
    }

    @Test
    public void loginUser() {
    }
    
}