package ru.practicum.shareit.user;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public List<UserDto> getAll() {
        return service.getAll();
    }

    @GetMapping("/{userId}")
    public UserDto get(@PathVariable Long userId) {
        return service.get(userId);
    }

    @PostMapping
    public UserDto save(@RequestBody UserDto userDto) {
        return service.save(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Long userId,
                          @RequestBody String updatedFields) {
        return service.update(userId, updatedFields);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        service.delete(userId);
    }
}
