package ch.uzh.ifi.seal.soprafs19.controller;

import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.exceptions.LoginException;
import ch.uzh.ifi.seal.soprafs19.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoginController {

    private final UserService service;

    LoginController(UserService service) {
        this.service = service;
    }

    @PostMapping("/login")
    @ExceptionHandler({LoginException.class})
    User loginUser(@RequestBody User loginUser) {
        return service.attemptLogin(loginUser);
    }

}
