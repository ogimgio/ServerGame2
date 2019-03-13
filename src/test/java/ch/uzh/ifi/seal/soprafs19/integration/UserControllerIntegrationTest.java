package ch.uzh.ifi.seal.soprafs19.integration;

import ch.uzh.ifi.seal.soprafs19.Application;
import ch.uzh.ifi.seal.soprafs19.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs19.controller.UserController;
import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;
import ch.uzh.ifi.seal.soprafs19.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.*;

@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(classes= Application.class)
public class UserControllerIntegrationTest {

    private static String testUsername = "testUsername";
    private static String testUsername2 = "testUsername2";
    private static String testName = "testName";
    private static String testName2 = "testName2";
    private static String testPassword = "1234";
    private static String testPassword2 = "asdf";
    private static LocalDate testBirthday = LocalDate.parse("1992-03-28");
    private static LocalDate testBirthday2 = LocalDate.parse("2001-10-02");

    @Autowired
    private UserService userService;

    @Autowired
    private UserController userController;

    @Autowired
    private UserRepository userRepository;

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
            testUser.setName(testName);
            testUser.setUsername(testUsername);
            testUser.setPassword(testPassword);
            testUser.setBirthday(testBirthday);
            testUser.setToken("testToken");
            testUser.setStatus(UserStatus.OFFLINE);
            testUser.setRegistrationDate(LocalDate.now());
            testUser.setLastSeenDate(LocalDateTime.now());
        } else if (i == 2) {
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

    @Test
    public void POST_createUser_BodyIsOk_then201IsReceived() throws IOException {
        this.setup();
        User testUser = createTestUser(1);

        // User -> Json
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(testUser);

        // Request body
        HttpEntity entity = new StringEntity(body);
        // Request url
        HttpPost request = new HttpPost("http://localhost:8080/users");

        // add body and header
        request.setEntity(entity);
        request.addHeader("Content-Type", "application/json");

        // Execute request and save response
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        HttpResponse response = httpclient.execute(request);

        // Test response according to assignment rest spec
        Assert.assertThat(response.getStatusLine().getStatusCode(), equalTo(HttpStatus.CREATED.value()));
        Assert.assertThat(response.getHeaders("Location")[0].getElements()[0].getValue(),
                containsString("/users?id="));
    }

    @Test
    public void POST_createUser_UsernameAlreadyInDatabase_then409IsReceived() throws IOException {
        this.setup();
        User testUser = createTestUser(1);

        // Create user beforehand -> expecting 409: "Username already exists in database"
        userService.createUser(testUser);

        // User -> Json
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(testUser);

        // Request body
        HttpEntity entity = new StringEntity(body);
        // Request url
        HttpPost request = new HttpPost("http://localhost:8080/users");

        // add body and header
        request.setEntity(entity);
        request.addHeader("Content-Type", "application/json");

        // Execute request and save response
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        HttpResponse response = httpclient.execute(request);

        // Test response according to expectations
        Assert.assertThat(response.getStatusLine().getStatusCode(), equalTo(HttpStatus.CONFLICT.value()));
        String responseBody = mapper.readValue(response.getEntity().getContent(), String.class);
        Assert.assertEquals(responseBody, "Username already exists in database");
    }

    @Test
    public void GET_userFromID_IdIsFound_then200IsReceived() throws IOException {
        this.setup();
        User testUser = createTestUser(1);

        // Create user beforehand -> Expecting 200, no body
        userService.createUser(testUser);


        // Request url
        HttpGet request = new HttpGet("http://localhost:8080/users/" + testUser.getId().toString());

        // add header
        request.addHeader("Content-Type", "application/json");

        // Execute request and save response
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        HttpResponse response = httpclient.execute(request);
        ObjectMapper mapper = new ObjectMapper();
        User returnedUser = mapper.readValue(response.getEntity().getContent(), User.class);

        // Test response according to expectations
        Assert.assertThat(response.getStatusLine().getStatusCode(), equalTo(HttpStatus.OK.value()));
        Assert.assertEquals(testUser, returnedUser);
    }

    @Test
    public void GET_userFromID_IdIsNotFound_then404IsReceived() throws IOException {
        this.setup();
        User testUser = createTestUser(1);

        // Create user beforehand -> Expecting 404, error msg (String) as body
        userService.createUser(testUser);


        // Request url with wrong Id ->
        Long wrongId = testUser.getId() + 1;
        HttpGet request = new HttpGet("http://localhost:8080/users/" + wrongId.toString());

        // add header
        request.addHeader("Content-Type", "application/json");

        // Execute request and save response
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        HttpResponse response = httpclient.execute(request);
        ObjectMapper mapper = new ObjectMapper();
        String responseBody = mapper.readValue(response.getEntity().getContent(), String.class);

        // Test response according to expectations
        Assert.assertThat(response.getStatusLine().getStatusCode(), equalTo(HttpStatus.NOT_FOUND.value()));
        Assert.assertEquals(responseBody, "User with userID " + wrongId + " not found in database");
    }

    @Test
    public void PUT_changeUserWithIDandToken_correctInfo_then204isReceived() throws IOException {
        this.setup();
        User testUser = createTestUser(1);

        // Create user beforehand
        userService.createUser(testUser);

        // Make changes to username and birthday
        testUser.setUsername("asdf");
        testUser.setBirthday(LocalDate.parse("2010-02-03"));


        // User -> Json
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(testUser);

        // Request body
        HttpEntity entity = new StringEntity(body);
        // Request url
        HttpPut request = new HttpPut("http://localhost:8080/users/" + testUser.getId().toString());

        // add body
        request.setEntity(entity);

        // add headers
        request.addHeader("Content-Type", "application/json");
        request.addHeader("token", testUser.getToken());

        // Execute request and save response
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        HttpResponse response = httpclient.execute(request);

        // Test response according to expectations
        Assert.assertThat(response.getStatusLine().getStatusCode(), equalTo(HttpStatus.NO_CONTENT.value()));
        Assert.assertEquals(response.getEntity().getContentLength(), 0L);
    }

    @Test
    public void PUT_changeUserWithIDandToken_incorrectID_then404isReceived() throws IOException {
        this.setup();
        User testUser = createTestUser(1);

        // Create user beforehand
        userService.createUser(testUser);

        // Make changes to username and birthday
        testUser.setUsername("asdf");
        testUser.setBirthday(LocalDate.parse("2010-02-03"));


        // User -> Json
        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(testUser);

        // Request body
        HttpEntity entity = new StringEntity(body);
        // Wrong Id
        Long wrongId = testUser.getId() + 1;
        // Request url
        HttpPut request = new HttpPut("http://localhost:8080/users/" + wrongId.toString());

        // add body
        request.setEntity(entity);

        // add headers
        request.addHeader("Content-Type", "application/json");
        request.addHeader("token", testUser.getToken());

        // Execute request and save response
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        HttpResponse response = httpclient.execute(request);
        String responseBody = mapper.readValue(response.getEntity().getContent(), String.class);

        // Test response according to expectations
        Assert.assertThat(response.getStatusLine().getStatusCode(), equalTo(HttpStatus.NOT_FOUND.value()));
        Assert.assertEquals(responseBody, "User with id " + wrongId.toString() + "not found");
    }

}
