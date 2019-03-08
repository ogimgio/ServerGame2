package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.exceptions.ConflictException;
import ch.uzh.ifi.seal.soprafs19.exceptions.NotFoundException;
import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;
import org.aspectj.weaver.ast.Not;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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

    public User createUser(User newUser) throws ConflictException {
        // Check new user for any errors
        if(newUser.getPassword().length() < 4) {
            throw new ConflictException("Password must be at least four characters long");
        }
        else if(newUser.getUsername().length() == 0) {
            throw new ConflictException("Username must not be empty");
        }
        else if(newUser.getName().length() == 0) {
            throw new ConflictException("Name must not be empty");
        }
        else if(userRepository.findByUsername(newUser.getUsername()) != null) {
            throw new ConflictException("Username already exists in database");
        }
        newUser.setToken(UUID.randomUUID().toString());
        newUser.setStatus(UserStatus.ONLINE);
        newUser.setRegistrationDate(LocalDate.now());
        newUser.setLastSeenDate(LocalDateTime.now());
        userRepository.save(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    public User getUser(long id) throws NotFoundException {
        User targetUser = this.userRepository.findById(id);
        if (targetUser == null) {
            throw new NotFoundException("User with userID " + id);
        } else {
            return this.userRepository.findById(id);
        }
    }

    public void deleteUser(long id) {
        User user = this.userRepository.findById(id);
        this.userRepository.delete(user);
    }

    public User attemptLogin(User loginUser) throws ConflictException {
        User targetUser = this.userRepository.findByUsername(loginUser.getUsername());
        if (targetUser != null) {
            if (targetUser.getPassword().equals(loginUser.getPassword())) {
                targetUser.setStatus(UserStatus.ONLINE);
                targetUser.setLastSeenDate((LocalDateTime.now()));
                return targetUser;
            } else {
                throw new ConflictException("Incorrect password");
            }
        } else {
            throw new ConflictException("User not found");
        }
    }

    public User changeUser(long id, User changeUser) throws ConflictException, NotFoundException {
        User targetUser = this.userRepository.findById(id);
        if (targetUser != null && changeUser.getId() == id) {
            LocalDate newBirthday = changeUser.getBirthday();
            String newUsername = changeUser.getUsername();
            if (newBirthday != null) {
                targetUser.setBirthday(newBirthday);
                log.debug("Changed birthday for user: {}", targetUser);
            }
            if (newUsername != null) {
                targetUser.setUsername(newUsername);
                log.debug("Changed username for user: {}", targetUser);
            }
            return targetUser;
        } else {
            throw new NotFoundException("User with id " + id + "not found");
        }
    }
}
