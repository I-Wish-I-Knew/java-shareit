package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {

    private final UserClient client;

    @Autowired
    public UserController(UserClient client) {
        this.client = client;
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAll() {
        log.info("Get users");
        return client.getAll();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> get(@PathVariable Long userId) {
        log.info("Get user {}", userId);
        return client.get(userId);
    }

    @PostMapping
    public ResponseEntity<UserDto> save(@RequestBody @Valid UserDto userDto) {
        return client.save(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> update(@PathVariable Long userId,
                                          @RequestBody String updatedFields) {
        log.info("Update user {} fields {}", userId, updatedFields);
        return client.update(userId, updatedFields);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        log.info("Delete user {}", userId);
        client.delete(userId);
    }
}
