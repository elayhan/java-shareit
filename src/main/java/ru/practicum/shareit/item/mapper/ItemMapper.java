package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;

@Mapper
public interface ItemMapper {
    @Mapping(target = "owner", source = "owner.id")
    ItemDto toItemDto(Item item);

    @Mapping(target = "owner", source = "user")
    @Mapping(target = "id", source = "itemDto.id")
    @Mapping(target = "name", source = "itemDto.name")
    Item toItem(ItemDto itemDto, User user);

    List<ItemDto> toListItemDto(Collection<Item> items);

}
