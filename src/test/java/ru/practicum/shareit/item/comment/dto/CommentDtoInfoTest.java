package ru.practicum.shareit.item.comment.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoInfoTest {

    @Autowired
    private JacksonTester<CommentDtoInfo> json;

    @Test
    void testCommentDtoInfo() throws IOException {
        CommentDtoInfo commentDtoInfo = new CommentDtoInfo(
                1L,
                "text",
                "name",
                LocalDateTime.now().withNano(0)
        );

        JsonContent<CommentDtoInfo> result = json.write(commentDtoInfo);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(commentDtoInfo.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.text")
                .isEqualTo(commentDtoInfo.getText());
        assertThat(result).extractingJsonPathStringValue("$.authorName")
                .isEqualTo(commentDtoInfo.getAuthorName());
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo(commentDtoInfo.getCreated().toString());
    }
}