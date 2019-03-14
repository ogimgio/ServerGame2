package ch.uzh.ifi.seal.soprafs19.controller;

import ch.uzh.ifi.seal.soprafs19.Application;
import ch.uzh.ifi.seal.soprafs19.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.exceptions.ConflictException;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.is;

@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(classes= Application.class)
public class UserControllerTest {

    private static String testUsername = "testUsername";
    private static String testUsername2 = "testUsername2";
    private static String testName = "testName";
    private static String testName2 = "testName2";
    private static String testPassword = "1234";
    private static String testPassword2 = "asdf";
    private static LocalDate testBirthday = LocalDate.parse("1992-03-28");
    private static LocalDate testBirthday2 = LocalDate.parse("2001-10-02");


    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserController userController;

    private User createTestUser(String name, String username, String password, LocalDate birthday) {
        User testUser = new User();
        testUser.setName(name);
        testUser.setUsername(username);
        testUser.setPassword(password);
        testUser.setBirthday(birthday);
        testUser.setToken(UUID.randomUUID().toString());
        testUser.setStatus(UserStatus.OFFLINE);
        testUser.setRegistrationDate(LocalDate.now());
        testUser.setLastSeenDate(LocalDateTime.now());

        return testUser;
    }

    private String setup() {
        this.userRepository.deleteAll();
        Assert.assertNotNull(this.userRepository);
        Assert.assertNotNull(this.userController);
        Assert.assertNotNull(this.userService);
        Assert.assertThat(userRepository.findByUsername("testUsername"), anyOf(is(nullValue()), is(Optional.empty())));
        Assert.assertThat(userRepository.findByUsername("testUsername2"), anyOf(is(nullValue()), is(Optional.empty())));

        User testUser1 = this.createTestUser(testName, testUsername, testPassword, testBirthday);
        User testUser2 = this.createTestUser(testName2, testUsername2, testPassword2, testBirthday2);
        testUser1.setId(2L);
        testUser2.setId(3L);

        this.userRepository.save(testUser1);
        this.userRepository.save(testUser2);

        return testUser1.getToken();
    }

    @Test
    public void all() {
        String token = this.setup();

        Iterable<User> results = this.userController.all(token);
        for(User u: results) {
            Assert.assertThat(u.getName(), anyOf(is(testName), is(testName2)));
            Assert.assertThat(u.getUsername(), anyOf(is(testUsername), is(testUsername2)));
            Assert.assertThat(u.getPassword(), anyOf(is(testPassword), is(testPassword2)));
            Assert.assertThat(u.getBirthday(), anyOf(is(testBirthday), is(testBirthday2)));
        }

        this.userRepository.deleteAll();
    }

    @Test
    public void getUser() {
        String token = this.setup();

        Long id = this.userRepository.findByName(testName).getId();

        User result = this.userController.getUser(id, token);

        Assert.assertEquals(testName, result.getName());
        Assert.assertEquals(testUsername, result.getUsername());
        Assert.assertEquals("1234", result.getPassword());
        Assert.assertEquals(testBirthday, result.getBirthday());

        this.userRepository.deleteAll();
    }

    @Test
    public void deleteUser() {
        this.setup();

        User deletion = this.userRepository.findByName(testName);
        Long id = deletion.getId();
        String token = deletion.getToken();

        this.userController.deleteUser(id, token);

        Assert.assertNull(this.userRepository.findByName(testName));

        this.userRepository.deleteAll();
    }

    @Test
    public void changeUser() {
        String token = this.setup();

        Long id = this.userRepository.findByName(testName).getId();

        User changeUser = new User();
        changeUser.setId(id);
        changeUser.setUsername("hans");
        changeUser.setBirthday(LocalDate.parse("1981-02-20"));
        changeUser.setToken(this.userRepository.findByName(testName).getToken());

        this.userController.changeUser(id, changeUser);

        User result = this.userRepository.findByName(testName);

        Assert.assertEquals("hans", result.getUsername());
        Assert.assertEquals(LocalDate.parse("1981-02-20"), result.getBirthday());

        this.userRepository.deleteAll();
    }

    @Test
    public void createUser() {
        User testUser = new User();
        testUser.setId(1L);
        testUser.setName(testName);
        testUser.setUsername(testUsername);
        testUser.setPassword(testPassword);
        testUser.setBirthday(testBirthday);

        this.userController.createUser(testUser);

        Assert.assertNotNull(this.userRepository.findByName(testName));

        this.userRepository.deleteAll();
    }

    @Test
    public void loginUser() {
        this.setup();

        User testLoginUser = new User();
        testLoginUser.setUsername(testUsername);
        testLoginUser.setPassword(testPassword);

        User result = this.userController.loginUser(testLoginUser);

        Assert.assertNotNull(result);
        Assert.assertEquals(testName, result.getName());

        this.userRepository.deleteAll();
    }

    @Test(expected = ConflictException.class)
    public void loginFail() {
        this.setup();

        User testLoginUser = new User();
        testLoginUser.setUsername(testUsername);
        testLoginUser.setPassword("wrongPassword");

        try {
            User result = this.userController.loginUser(testLoginUser);
        } catch (ConflictException e) {
            Assert.assertEquals("Incorrect password", e.getMessage());
            e.setMessage("asdf");
            Assert.assertEquals("asdf", e.getMessage());
            throw e;
        }

        this.userRepository.deleteAll();
    }

    @Test(expected = NotFoundException.class)
    public void getFail() {
        String token = this.setup();

        try {
            User result = this.userController.getUser(29L, token);
        } catch (NotFoundException e) {
            Assert.assertEquals("User with userID 29 not found in database", e.getMessage());
            e.setMessage("asdf");
            Assert.assertEquals("asdf", e.getMessage());
            throw e;
        }
    }
}