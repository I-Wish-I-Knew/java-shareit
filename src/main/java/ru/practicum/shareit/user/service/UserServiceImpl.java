package ru.practicum.shareit.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.converter.UserConverter;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserStorage storage;
    private final UserConverter converter;

    private long userId;

    @Autowired
    public UserServiceImpl(UserStorage storage, UserConverter converter) {
        this.storage = storage;
        this.converter = converter;
        this.userId = 0;
    }

    @Override
    public List<UserDto> getAll() {
        List<User> users = storage.getAll();
        return users.stream()
                .map(converter::convertToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto get(Long id) {
        checkContainsInStorage(id);
        return converter.convertToUserDto(storage.get(id));
    }

    @Override
    public UserDto save(UserDto userDto) {
        User user = converter.convertToUser(userDto);
        validateUserForSave(user);
        user.setId(++userId);
        storage.save(user);
        return converter.convertToUserDto(user);
    }

    @Override
    public UserDto update(Long id, String updatedFields) {
        checkContainsInStorage(id);
        UserDto updatedUser = get(id);
        ObjectMapper mapper = new ObjectMapper();
        try {
            updatedUser = mapper.readerForUpdating(updatedUser).readValue(updatedFields);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        validateUserForUpdate(updatedUser);
        storage.update(converter.convertToUser(updatedUser));
        return updatedUser;
    }

    @Override
    public void delete(Long id) {
        checkContainsInStorage(id);
        storage.delete(id);
    }

    private void checkContainsInStorage(Long userId) {
        if (!storage.containsInStorage(userId)) {
            throw new NotFoundException(String.format("Пользователь с id - %d не найден", userId));
        }
    }

    private void validateUserForUpdate(UserDto user) {
        if (storage.getAll().stream().anyMatch(u -> u.getEmail().equals(user.getEmail())
                && !u.getId().equals(user.getId()))) {
            throw new AlreadyExistsException
                    (String.format("Пользователь с email - %s уже существует", user.getEmail()));
        }
    }
    private void validateUserForSave(User user) {
        if (storage.getAll().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            throw new AlreadyExistsException
                    (String.format("Пользователь с email - %s уже существует", user.getEmail()));
        }
    }
}
