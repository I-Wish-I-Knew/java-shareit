package ru.practicum.shareit.item.comment.dto;

import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class CommentMapper {

    private CommentMapper() {
    }

    public static CommentDto convertToCommentDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText());
    }

    public static Comment convertToComment(CommentDto commentDto, Item item, User author) {
        return new Comment(commentDto.getId(),
                commentDto.getText(),
                item,
                author,
                LocalDateTime.now());
    }

    public static CommentDtoInfo convertToCommentDtoInfo(Comment comment, User author) {
        return new CommentDtoInfo(comment.getId(),
                comment.getText(),
                author.getName(),
                comment.getCreated());
    }
}
