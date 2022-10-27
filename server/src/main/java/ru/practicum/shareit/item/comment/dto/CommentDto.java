package ru.practicum.shareit.item.comment.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class CommentDto {
    private Long id;
    private String text;
}
