package ru.practicum.shareit.user.dto;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

@Component
public class UserMapper {

    private final ModelMapper mapper;

    public UserMapper() {
        this.mapper = new ModelMapper();
    }

    public UserDto convertToUserDto(User user) {
        return mapper.map(user, UserDto.class);
    }

    public User convertToUser(UserDto userDto) {
        return mapper.map(userDto, User.class);
    }
}
