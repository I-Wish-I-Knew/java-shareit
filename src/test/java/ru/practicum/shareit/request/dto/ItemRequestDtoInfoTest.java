package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoInfoTest {

    @Autowired
    JacksonTester<ItemRequestDtoInfo> json;

    @Test
    void testItemRequestDtoInfo() throws IOException {
        ItemRequestDtoInfo itemRequestDtoInfo = new ItemRequestDtoInfo(
                1L,
                "description",
                LocalDateTime.now().withNano(0),
                Collections.singletonList(new ItemRequestDtoInfo.Item(1L,
                        "itemName",
                        "itemDescription",
                        true,
                        1L))
        );

        JsonContent<ItemRequestDtoInfo> result = json.write(itemRequestDtoInfo);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(itemRequestDtoInfo.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemRequestDtoInfo.getDescription());
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo(itemRequestDtoInfo.getCreated().toString());
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id")
                .isEqualTo(itemRequestDtoInfo.getItems().get(0).getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.items[0].name")
                .isEqualTo(itemRequestDtoInfo.getItems().get(0).getName());
        assertThat(result).extractingJsonPathStringValue("$.items[0].description")
                .isEqualTo(itemRequestDtoInfo.getItems().get(0).getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.items[0].available")
                .isEqualTo(itemRequestDtoInfo.getItems().get(0).isAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.items[0].requestId")
                .isEqualTo(itemRequestDtoInfo.getItems().get(0).getRequestId().intValue());
    }
}