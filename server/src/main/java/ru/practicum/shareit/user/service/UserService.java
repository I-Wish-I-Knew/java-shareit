package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getAll();

    UserDto get(Long id);

    UserDto save(UserDto userDto);

    UserDto update(Long id, String updatedFields);

    void delete(Long id);
}
