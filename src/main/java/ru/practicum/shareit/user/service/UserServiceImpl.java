package ru.practicum.shareit.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UpdateFailedException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserStorage storage;
    private final UserMapper converter;
    public static final String USER_NOT_FOUND = "Пользователь c id - %d не найден";

    public UserServiceImpl(UserStorage storage, UserMapper converter) {
        this.storage = storage;
        this.converter = converter;
    }

    @Override
    public List<UserDto> getAll() {
        List<User> users = storage.findAll();
        return users.stream()
                .map(converter::convertToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto get(Long id) {
        User user = storage.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(USER_NOT_FOUND, id)));
        return converter.convertToUserDto(user);
    }

    @Transactional
    @Override
    public UserDto save(UserDto userDto) {
        User user = converter.convertToUser(userDto);
        storage.save(user);
        return converter.convertToUserDto(user);
    }

    @Transactional
    @Override
    public UserDto update(Long id, String updatedFields) {
        User updatedUser = storage.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(USER_NOT_FOUND, id)));
        ObjectMapper mapper = new ObjectMapper();
        try {
            updatedUser = mapper.readerForUpdating(updatedUser).readValue(updatedFields);
        } catch (JsonProcessingException e) {
            throw new UpdateFailedException("Не удалось обновить данные");
        }
        storage.save(updatedUser);
        return converter.convertToUserDto(updatedUser);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        storage.deleteById(id);
    }

}
