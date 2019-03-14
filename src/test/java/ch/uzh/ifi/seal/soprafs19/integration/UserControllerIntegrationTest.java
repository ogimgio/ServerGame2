package ch.uzh.ifi.seal.soprafs19.integration;

import ch.uzh.ifi.seal.soprafs19.Application;
import ch.uzh.ifi.seal.soprafs19.TestUser;
import ch.uzh.ifi.seal.soprafs19.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs19.controller.UserController;
import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;
import ch.uzh.ifi.seal.soprafs19.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class UserControllerIntegrationTest {

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

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void mockSetup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void givenWac_whenServletContext_thenItProvidesGreetController() {
        ServletContext servletContext = wac.getServletContext();

        Assert.assertNotNull(servletContext);
        Assert.assertTrue(servletContext instanceof MockServletContext);
        Assert.assertNotNull(wac.getBean("userController"));
    }

    @Test
    public void POST_createUser_BodyIsOk_then201IsReceived() throws Exception {
        this.setup();
        User testUser = createTestUser(1);

        String body = this.composeBody(testUser);

        this.mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(body))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/users?id=" + testUser.getId().toString()));
    }

    @Test
    public void POST_createUser_UsernameAlreadyInDatabase_then409IsReceived() throws Exception {
        this.setup();
        User testUser = createTestUser(1);
        this.userService.createUser(testUser);

        String body = this.composeBody(testUser);

        this.mockMvc.perform(post("/users").header("Content-Type", "application/json")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(body))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message").value("Username already exists in database"));
    }

    @Test
    public void GET_userFromID_IdIsFound_then200IsReceived() throws Exception {
        this.setup();
        User testUser = createTestUser(1);
        this.userService.createUser(testUser);

        this.mockMvc.perform(get("/users/" + testUser.getId().toString())
                .header("Content-Type", "application/json")
                .header("token", testUser.getToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.username").value(testUser.getUsername()))
                .andExpect(jsonPath("$.token").value(testUser.getToken()));
    }

    @Test
    public void GET_userFromID_IdIsNotFound_then404IsReceived() throws Exception {
        this.setup();
        User testUser = createTestUser(1);
        this.userService.createUser(testUser);

        Long wrongId = testUser.getId() + 1;

        this.mockMvc.perform(get("/users/" + wrongId.toString())
                .header("Content-Type", "application/json")
                .header("token", testUser.getToken()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message").value("User with userID " + wrongId + " not found in database"));
    }

    @Test
    public void PUT_changeUserWithIDandToken_correctInfo_then204isReceived() throws Exception {
        this.setup();
        User testUser = createTestUser(1);

        // Create user beforehand
        userService.createUser(testUser);

        // Make changes to username and birthday
        testUser.setUsername("asdf");
        testUser.setBirthday(LocalDate.parse("2010-02-03"));


        // User -> Json
        String body = this.composeBody(testUser);

        this.mockMvc.perform(get("/users/" + testUser.getId().toString())
                .header("token", testUser.getToken())
                .header("Content-Type", "application/json")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(body))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void PUT_changeUserWithIDandToken_incorrectID_then404isReceived() throws Exception {
        this.setup();
        User testUser = createTestUser(1);

        // Create user beforehand
        userService.createUser(testUser);

        // Make changes to username and birthday
        testUser.setUsername("asdf");
        testUser.setBirthday(LocalDate.parse("2010-02-03"));


        // User -> Json
        String body = this.composeBody(testUser);

        Long wrongId = testUser.getId() + 1;

        this.mockMvc.perform(get("/users/" + wrongId.toString())
                .header("token", testUser.getToken())
                .header("Content-Type", "application/json")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(body))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User with userID " + wrongId + " not found in database"));
    }

    private void setup() {
        this.userRepository.deleteAll();
        Assert.assertNotNull(this.userRepository);
        Assert.assertNotNull(this.userController);
        Assert.assertNotNull(this.userService);
        Assert.assertThat(userRepository.findByUsername("testUsername"), anyOf(is(nullValue()), is(Optional.empty())));
        Assert.assertThat(userRepository.findByUsername("testUsername2"), anyOf(is(nullValue()), is(Optional.empty())));
    }

    private User createTestUser(int i) {
        User testUser = new User();
        if (i == 1) {
            // This user ID is set to 2 because most likely the default root user in UserService @PostConstruct init
            // method gets ID 1
            testUser.setId(2L);
            testUser.setName(testName);
            testUser.setUsername(testUsername);
            testUser.setPassword(testPassword);
            testUser.setBirthday(testBirthday);
            testUser.setToken("testToken");
            testUser.setStatus(UserStatus.OFFLINE);
            testUser.setRegistrationDate(LocalDate.now());
            testUser.setLastSeenDate(LocalDateTime.now());
        } else if (i == 2) {
            // This user ID is set to 4 because to generate wrong ID requests we increment user IDs by 1, this would
            // result in the ID being 3 for test user 1
            testUser.setId(4L);
            testUser.setName(testName2);
            testUser.setUsername(testUsername2);
            testUser.setPassword(testPassword2);
            testUser.setBirthday(testBirthday2);
            testUser.setToken("testToken");
            testUser.setStatus(UserStatus.OFFLINE);
            testUser.setRegistrationDate(LocalDate.now());
            testUser.setLastSeenDate(LocalDateTime.now());
        } else {
            return null;
        }
        return testUser;
    }

    private String composeBody(User user) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        TestUser testUser = new TestUser(user);
        String body = mapper.writeValueAsString(testUser);
        return body;
    }
}
