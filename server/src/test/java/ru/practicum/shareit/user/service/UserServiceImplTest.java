package ru.practicum.shareit.user.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    private UserService service;
    @Mock
    private UserStorage storage;
    private User user;

    @BeforeEach
    void setUp() {
        service = new UserServiceImpl(storage);
        user = new User(1L,
                "user 1",
                "user1@email");
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(storage);
    }

    @Test
    void getAll() {
        when(storage.findAll()).thenReturn(Collections.singletonList(user));

        final List<UserDto> users = service.getAll();
        Assertions.assertThat(users).isNotNull()
                .hasSize(1)
                .contains(UserMapper.convertToUserDto(user));

        verify(storage, times(1))
                .findAll();
    }

    @Test
    void get() {
        when(storage.findById(anyLong())).thenReturn(Optional.of(user));

        final UserDto newUser = service.get(1L);
        assertThat(newUser).isNotNull()
                .isEqualTo(UserMapper.convertToUserDto(user));

        verify(storage, times(1))
                .findById(1L);
    }

    @Test
    void save() {
        when(storage.save(any())).thenReturn(user);

        final UserDto savedUser = service.save(new UserDto());
        assertThat(savedUser).isNotNull()
                .isEqualTo(UserMapper.convertToUserDto(user));

        verify(storage, times(1))
                .save(any());
    }

    @Test
    void update() {
        final User updatedUser = new User(user.getId(), "userUpdated1", user.getEmail());
        when(storage.findById(updatedUser.getId())).thenReturn(Optional.of(user));
        when(storage.save(any())).thenReturn(updatedUser);

        final UserDto updatedUserResult = service.update(1L,
                "{\"name\": \"userUpdated1\"}");
        assertThat(updatedUserResult).isNotNull()
                .isEqualTo(UserMapper.convertToUserDto(updatedUser));

        verify(storage, times(1))
                .findById(updatedUser.getId());
        verify(storage, times(1))
                .save(updatedUser);
    }

    @Test
    void delete() {
        service.delete(1L);

        verify(storage, times(1))
                .deleteById(1L);
    }
}