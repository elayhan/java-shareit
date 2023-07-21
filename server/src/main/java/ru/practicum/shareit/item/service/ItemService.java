package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Long userId, ItemDto itemDto);

    ItemDto getItem(Long userId, Long id);

    List<ItemDto> getAllItems(Long userId, Integer from, Integer size);

    List<ItemDto> searchItem(Long userId, String match, Integer from, Integer size);

    ItemDto updateItem(Long userId, Long id, ItemDto itemDto);

    CommentDto addComment(Long userId, Long id, CommentDto commentDto);
}
