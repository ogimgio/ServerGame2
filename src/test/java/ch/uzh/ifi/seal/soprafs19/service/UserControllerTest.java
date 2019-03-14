package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.Application;
import ch.uzh.ifi.seal.soprafs19.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs19.controller.UserController;
import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.exceptions.ExceptionLogin;
import ch.uzh.ifi.seal.soprafs19.exceptions.NotFoundException;
import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;
import ch.uzh.ifi.seal.soprafs19.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */
@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(classes= Application.class)

public class UserControllerTest {
    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserController userController;

    @Test(expected = ExceptionLogin.class)
    public void createuser(){
        User testUser = new User();
        testUser.setPassword("testpassword11");
        testUser.setUsername("testUsername11");
        userController.createUser(testUser);

        Assert.assertNotNull(testUser.getToken());

        User testUser12 = new User();
        testUser12.setPassword("testpassword11");
        testUser12.setUsername("testUsername11");
        userController.createUser(testUser12); //throw exception



    }
    @Test(expected = NotFoundException.class)
    public void updateuser(){
        User testUser13 = new User();
        testUser13.setPassword("testpassword13");
        testUser13.setUsername("testUsername13");
        userController.createUser(testUser13);

        userController.updateUser(10,testUser13); //throw exception
    }
    @Test
    public void loginUser(){
        User testUser14 = new User();
        testUser14.setPassword("testpassword14");
        testUser14.setUsername("testUsername14");
        userController.createUser(testUser14);

        User loggedUser = userController.loginUser(testUser14);
        Assert.assertEquals(testUser14, loggedUser);
    }

}

