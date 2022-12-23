package ru.practicum.shareit.request.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class ItemRequestDtoInfo {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<Item> items;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class Item {
        private Long id;
        private String name;
        private String description;
        private boolean available;
        private Long requestId;
    }
}
