package ru.practicum.shareit.user.converter;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

@Component
public class UserConverter {

    private final ModelMapper mapper;

    public UserConverter() {
        this.mapper = new ModelMapper();
    }

    public UserDto convertToUserDto(User user) {
        return mapper.map(user, UserDto.class);
    }

    public User convertToUser(UserDto userDto) {
        return mapper.map(userDto, User.class);
    }
}
