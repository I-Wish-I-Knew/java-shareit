package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoInfoTest {

    @Autowired
    private JacksonTester<ItemDtoInfo> json;

    @Test
    void testItemDto() throws Exception {
        ItemDtoInfo.Comment comment = new ItemDtoInfo.Comment(
                1L,
                "text",
                "name",
                LocalDateTime.now().withNano(0));
        ItemDtoInfo itemDtoInfo = new ItemDtoInfo(
                1L,
                "item",
                "description",
                true,
                new ItemDtoInfo.Booking(1L, 1L),
                new ItemDtoInfo.Booking(2L, 2L),
                Collections.singletonList(comment)
        );

        JsonContent<ItemDtoInfo> result = json.write(itemDtoInfo);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(itemDtoInfo.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.name")
                .isEqualTo(itemDtoInfo.getName());
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemDtoInfo.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(itemDtoInfo.getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id")
                .isEqualTo(itemDtoInfo.getLastBooking().getId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId")
                .isEqualTo(itemDtoInfo.getLastBooking().getBookerId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id")
                .isEqualTo(itemDtoInfo.getNextBooking().getId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId")
                .isEqualTo(itemDtoInfo.getNextBooking().getBookerId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id")
                .isEqualTo(itemDtoInfo.getComments().get(0).getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text")
                .isEqualTo(itemDtoInfo.getComments().get(0).getText());
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName")
                .isEqualTo(itemDtoInfo.getComments().get(0).getAuthorName());
        assertThat(result).extractingJsonPathStringValue("$.comments[0].created")
                .isEqualTo(itemDtoInfo.getComments().get(0).getCreated().toString());
    }
}