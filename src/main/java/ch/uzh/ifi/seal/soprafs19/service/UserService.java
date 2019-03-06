package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.exceptions.LoginException;
import ch.uzh.ifi.seal.soprafs19.exceptions.RegistrationException;
import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.Registration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;


    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Iterable<User> getUsers() {
        return this.userRepository.findAll();
    }

    public User createUser(User newUser) throws RegistrationException {
        // Check new user for any errors
        if(newUser.getPassword().length() < 4) {
            throw new RegistrationException("Password must be at least four characters long");
        }
        else if(newUser.getUsername().length() == 0) {
            throw new RegistrationException("Username must not be empty");
        }
        else if(newUser.getName().length() == 0) {
            throw new RegistrationException("Name must not be empty");
        }
        else if(userRepository.findByUsername(newUser.getUsername()) != null) {
            throw new RegistrationException("Username already exists in database");
        }
        newUser.setToken(UUID.randomUUID().toString());
        newUser.setStatus(UserStatus.ONLINE);
        newUser.setGenerationDate(LocalDateTime.now().toString());
        newUser.setLastSeenDate(LocalDateTime.now().toString());
        userRepository.save(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    public User getUser(long id) {
        return this.userRepository.findById(id);
    }

    public void deleteUser(long id) {
        User user = this.userRepository.findById(id);
        this.userRepository.delete(user);
    }

    public User attemptLogin(User loginUser) throws LoginException {
        User targetUser = this.userRepository.findByUsername(loginUser.getUsername());
        if (targetUser != null) {
            if (targetUser.getPassword().equals(loginUser.getPassword())) {
                targetUser.setStatus(UserStatus.ONLINE);
                targetUser.setLastSeenDate((LocalDateTime.now().toString()));
                return targetUser;
            } else {
                throw new LoginException("Incorrect password", loginUser.getUsername());
            }
        } else {
            throw new LoginException("User not found", loginUser.getUsername());
        }
    }
}
