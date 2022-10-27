package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserClient client;

    @Autowired
    public UserController(UserClient client) {
        this.client = client;
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAll() {
        return client.getAll();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> get(@PathVariable Long userId) {
        return client.get(userId);
    }

    @PostMapping
    public ResponseEntity<UserDto> save(@RequestBody @Valid UserDto userDto) {
        return client.save(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> update(@PathVariable Long userId,
                                          @RequestBody String updatedFields) {
        return client.update(userId, updatedFields);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        client.delete(userId);
    }
}
