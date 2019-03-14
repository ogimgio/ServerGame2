package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.Application;
import ch.uzh.ifi.seal.soprafs19.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs19.entity.User;
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
public class UserServiceTest {


    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
    public void createUser() {
        Assert.assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setPassword("testpassword");
        testUser.setUsername("testUsername");

        User createdUser = userService.createUser(testUser);

        Assert.assertNotNull(createdUser.getToken());
        Assert.assertEquals(createdUser.getStatus(),UserStatus.ONLINE);
        Assert.assertEquals(createdUser, userRepository.findByToken(createdUser.getToken()));
    }
   @Test
    public void getUser() {
       User testUser2 = new User();
       testUser2.setPassword("testpassword2");
       testUser2.setUsername("testUsername2");
       User createdUser = userService.createUser(testUser2);
       User gotUser = userService.getUser(createdUser.getId());

       Assert.assertNotNull(gotUser);
       Assert.assertEquals(createdUser.getUsername(), gotUser.getUsername());
       Assert.assertEquals(createdUser.getId(), gotUser.getId());
    }

    @Test
    public void logout() {
        User testUser3 = new User();
       testUser3.setPassword("testpassword2");
        testUser3.setUsername("testUsername3");
        User createdUser = userService.createUser(testUser3);
        Assert.assertEquals(createdUser.getStatus(), UserStatus.ONLINE);
        User newUser = userService.logOut(createdUser.getId(),createdUser);

        Assert.assertNotNull(newUser.getStatus());
        Assert.assertEquals(createdUser.getStatus(), newUser.getStatus());
    }
    @Test
    public void UpdateUser(){
        User testUser4 = new User();
        testUser4.setPassword("testpassword2");
        testUser4.setUsername("testUsername4");
        testUser4.SetBirthday("040404");
        userService.createUser(testUser4); //create user 4

        User testUser5 = new User();
        testUser5.setPassword("testpassword5");
        testUser5.setUsername("testUsername5");
        testUser5.SetBirthday("030303");

        testUser4 = userService.updateUser(testUser4.getId(),testUser5);
        Assert.assertEquals(testUser4.getBirthday(),testUser5.getBirthday());
        Assert.assertEquals(testUser4.getUsername(), testUser5.getUsername());

    }
    @Test
    public void getUserByUsername(){
        User testUser7 = new User();
        testUser7.setPassword("testpassword7");
        testUser7.setUsername("testUsername7");
        testUser7.SetBirthday("0404040");
        userService.createUser(testUser7);

        User testedUser = userService.getUserByUsername(testUser7.getUsername());
        Assert.assertEquals(testedUser.getUsername(),testUser7.getUsername());
    }
    @Test
    public void checkCredentsforLogin(){
        User testUser8 = new User();
        testUser8.setPassword("testpassword8");
        testUser8.setUsername("testUsername8");
        testUser8.SetBirthday("0404040");
        userService.createUser(testUser8);

        User testedUser8 = new User();
        testedUser8.setPassword("testpassword8");
        testedUser8.setUsername("testUsername8");
        testedUser8 = userService.checkCredentials(testedUser8);

        Assert.assertEquals(testUser8, testedUser8);
    }
}
