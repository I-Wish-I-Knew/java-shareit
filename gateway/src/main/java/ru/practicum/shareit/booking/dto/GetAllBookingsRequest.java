package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.State;

@Data
@NoArgsConstructor
public class GetAllBookingsRequest {
    private State state;
    private Long userId;
    private boolean isOwner;
    private Integer from;
    private Integer size;

    public static GetAllBookingsRequest of(State state,
                                           Long userId,
                                           boolean isOwner,
                                           Integer from,
                                           Integer size) {
        GetAllBookingsRequest request = new GetAllBookingsRequest();
        request.setState(state);
        request.setUserId(userId);
        request.setOwner(isOwner);
        request.setFrom(from);
        request.setSize(size);
        return request;
    }
}
