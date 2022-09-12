package ru.practicum.shareit.item.converter;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemConverter {

    private final ModelMapper mapper;

    public ItemConverter() {
        this.mapper = new ModelMapper();
    }

    public ItemDto convertToItemDto(Item item) {
        return mapper.map(item, ItemDto.class);
    }

    public Item convertToItem(ItemDto itemDto) {
        return mapper.map(itemDto, Item.class);
    }
}
