package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.Application;
import ch.uzh.ifi.seal.soprafs19.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.exceptions.ConflictException;
import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;
import ch.uzh.ifi.seal.soprafs19.service.UserService;
import org.assertj.core.internal.Iterables;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.hamcrest.CoreMatchers.*;

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

    private User createTestUser(String name, String username, String password) {
        User testUser = new User();
        testUser.setName(name);
        testUser.setUsername(username);
        testUser.setPassword(password);
        testUser.setRegistrationDate(LocalDate.now());
        testUser.setLastSeenDate(LocalDateTime.now());
        testUser.setToken(UUID.randomUUID().toString());
        testUser.setStatus(UserStatus.ONLINE);

        return testUser;
    }

    @Test
    public void createUser() {
        Assert.assertThat(userRepository.findByUsername("testUsername"), anyOf(is(nullValue()), is(Optional.empty())));

        User testUser = this.createTestUser("testName", "testUsername", "1234");

        User createdUser = userService.createUser(testUser);

        Assert.assertNotNull(createdUser.getToken());
        Assert.assertEquals(createdUser.getStatus(), UserStatus.ONLINE);
        Assert.assertEquals(createdUser, userRepository.findByToken(createdUser.getToken()));
        Assert.assertEquals(LocalDate.now(), createdUser.getRegistrationDate());

        this.userRepository.deleteAll();
    }

    @Test
    public void getUsers() {
        Assert.assertThat(userRepository.findByUsername("testUsername"), anyOf(is(nullValue()), is(Optional.empty())));
        Assert.assertThat(userRepository.findByUsername("testUsername2"), anyOf(is(nullValue()), is(Optional.empty())));

        User testUser = this.createTestUser("testName", "testUsername", "1234");
        userRepository.save(testUser);

        User testUser2 = this.createTestUser("testName2", "testUsername2", "2345");
        userRepository.save(testUser2);

        Iterable<User> gotUsers = userService.getUsers();

        List<User> result = StreamSupport.stream(gotUsers.spliterator(), false)
                .collect(Collectors.toList());

        Assert.assertEquals(testUser, result.get(0));
        Assert.assertEquals(testUser2, result.get(1));

        this.userRepository.deleteAll();
    }

    @Test
    public void getUser() {
        Assert.assertThat(userRepository.findByUsername("testUsername"), anyOf(is(nullValue()), is(Optional.empty())));

        User testUser = this.createTestUser("testName", "testUsername", "1234");
        userRepository.save(testUser);

        Assert.assertEquals(testUser, userService.getUser(testUser.getId()));
        testUser.seen();
        LocalDateTime seenTime = LocalDateTime.now();
        testUser.setLastSeenDate(seenTime);
        Assert.assertEquals(seenTime, testUser.getLastSeenDate());

        this.userRepository.deleteAll();
    }

    @Test
    public void deleteUser() {
        Assert.assertThat(userRepository.findByUsername("testUsername"), anyOf(is(nullValue()), is(Optional.empty())));

        User testUser = this.createTestUser("testName", "testUsername", "1234");
        userRepository.save(testUser);

        userService.deleteUser(testUser.getId(), testUser);

        Assert.assertEquals(Optional.empty(), userRepository.findById(testUser.getId()));
    }

    @Test
    public void attemptLogin() {
        Assert.assertThat(userRepository.findByUsername("testUsername"), anyOf(is(nullValue()), is(Optional.empty())));

        User testUser = this.createTestUser("testName", "testUsername", "1234");
        testUser.setStatus(UserStatus.OFFLINE);
        userRepository.save(testUser);


        User testLoginUser = new User();
        testLoginUser.setUsername("testUsername");
        testLoginUser.setPassword("1234");

        User loggedInUser = userService.attemptLogin(testLoginUser);

        Assert.assertEquals(testUser, loggedInUser);
        Assert.assertEquals(UserStatus.ONLINE, userRepository.findByUsername("testUsername").getStatus());

        this.userRepository.deleteAll();
    }

    @Test
    public void changeUser() {
        Assert.assertThat(userRepository.findByUsername("testUsername"), anyOf(is(nullValue()), is(Optional.empty())));

        User testUser = this.createTestUser("testName", "testUsername", "1234");
        testUser.setBirthday(LocalDate.now());
        userRepository.save(testUser);

        User testChangeUser = this.createTestUser("testName", "asdf", "1234");
        testChangeUser.setId(testUser.getId());
        testChangeUser.setBirthday(LocalDate.parse("1990-07-20"));

        User changedUser = userService.changeUser(testUser.getId(), testChangeUser);

        Assert.assertEquals(changedUser.getUsername(), "asdf");
        Assert.assertEquals(changedUser.getBirthday().toString(), "1990-07-20");

        this.userRepository.deleteAll();
    }
}
