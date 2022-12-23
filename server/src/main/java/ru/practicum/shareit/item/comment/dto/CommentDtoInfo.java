package ru.practicum.shareit.item.comment.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class CommentDtoInfo {
    private Long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}
