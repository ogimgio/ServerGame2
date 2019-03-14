package ch.uzh.ifi.seal.soprafs19;

import ch.uzh.ifi.seal.soprafs19.entity.User;

import java.io.Serializable;
import java.time.LocalDateTime;

public class TestUser implements Serializable {

    private Long id;
    private String name;
    private String username;
    private String password;
    private String token;
    private String status;
    private String birthday;
    private String registrationDate; // 1992-12-28
    private String lastSeenDate; // 1992-12-28T00:00:00

    public TestUser(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.token = user.getToken();
        this.status = user.getStatus().toString();
        this.birthday = user.getBirthday().toString();
        this.registrationDate = user.getRegistrationDate().toString();
        this.lastSeenDate = user.getLastSeenDate().toString();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLastSeenDate(String lastSeenDate) {
        this.lastSeenDate = lastSeenDate;
    }

    public String getLastSeenDate() {
        return lastSeenDate;
    }

    public void seen() {
        this.setLastSeenDate(LocalDateTime.now().toString());
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
}
