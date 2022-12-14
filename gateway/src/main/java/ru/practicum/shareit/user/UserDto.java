package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+.[a-z]+")
    private String email;
}
