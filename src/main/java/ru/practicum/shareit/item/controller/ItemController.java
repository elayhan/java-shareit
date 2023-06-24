package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.CustomHeader;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;

    @PostMapping
    public ItemDto createItem(@RequestHeader(CustomHeader.USER_ID) Long userId,
                              @Valid @RequestBody ItemDto itemDto) {
        return service.createItem(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@RequestHeader(CustomHeader.USER_ID) Long userId,
                              @PathVariable Long id,
                              @RequestBody ItemDto itemDto) {
        return service.updateItem(userId, id, itemDto);
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@RequestHeader(CustomHeader.USER_ID) Long userId,
                               @PathVariable Long id) {
        return service.getItem(userId, id);
    }

    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader(CustomHeader.USER_ID) Long userId) {
        return service.getAllItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestHeader(CustomHeader.USER_ID) Long userId, @RequestParam String text) {
        return service.searchItem(userId, text.toLowerCase());
    }

    @PostMapping("/{id}/comment")
    public CommentDto addComment(@RequestHeader(CustomHeader.USER_ID) Long userId, @PathVariable Long id, @Valid @RequestBody CommentDto commentDto) {
        return service.addComment(userId, id, commentDto);
    }

}
