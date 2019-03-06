package ch.uzh.ifi.seal.soprafs19.controller;

import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;
import ch.uzh.ifi.seal.soprafs19.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class UserController {

    private final UserService service;

    UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/users")
    Iterable<User> all() {
        return service.getUsers();
    }

    @GetMapping("/users/{nameUser}")
    User getUser(@PathVariable String nameUser) {return service.getUser(nameUser); }

    @PostMapping("/users")
    User createUser(@RequestBody User newUser) {
        return this.service.createUser(newUser);
    }

    @RequestMapping(value = "/auth", method = RequestMethod.POST)
    String checkCredentials(@RequestBody Map<String,String> parameters){
        String username = parameters.get("username");
        String password = parameters.get("password");
        String name = parameters.get("name");
        return this.service.checkCredentials(username, password, name);
    }

}
