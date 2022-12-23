package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoInfoTest {
    @Autowired
    private JacksonTester<BookingDtoInfo> json;

    @Test
    void testBookingDtoInfo() throws Exception {
        BookingDtoInfo bookingDtoInfo = new BookingDtoInfo(
                1L,
                LocalDateTime.now().plusMonths(1).withNano(0),
                LocalDateTime.now().plusMonths(2).withNano(0),
                BookingStatus.WAITING,
                new BookingDtoInfo.Item(1L, "item"),
                new BookingDtoInfo.User(1L)
        );

        JsonContent<BookingDtoInfo> result = json.write(bookingDtoInfo);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(bookingDtoInfo.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(bookingDtoInfo.getStart().toString());
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(bookingDtoInfo.getEnd().toString());
        assertThat(result).extractingJsonPathStringValue("$.status")
                .isEqualTo(bookingDtoInfo.getStatus().toString());
        assertThat(result).extractingJsonPathNumberValue("$.item.id")
                .isEqualTo(bookingDtoInfo.getItem().getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.item.name")
                .isEqualTo(bookingDtoInfo.getItem().getName());
        assertThat(result).extractingJsonPathNumberValue("$.booker.id")
                .isEqualTo(bookingDtoInfo.getBooker().getId().intValue());
    }
}