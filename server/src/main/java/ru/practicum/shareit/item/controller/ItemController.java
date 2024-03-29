package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.CustomHeader;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;

    @PostMapping
    public ItemDto createItem(@RequestHeader(CustomHeader.USER_ID) Long userId,
                              @RequestBody ItemDto itemDto) {
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
    public List<ItemDto> getAllItems(@RequestHeader(CustomHeader.USER_ID) Long userId,
                                     @RequestParam(defaultValue = "0") Integer from,
                                     @RequestParam(defaultValue = "10") Integer size) {
        return service.getAllItems(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestHeader(CustomHeader.USER_ID) Long userId,
                                    @RequestParam String text,
                                    @RequestParam(defaultValue = "0") Integer from,
                                    @RequestParam(defaultValue = "10") Integer size) {
        return service.searchItem(userId, text.toLowerCase(), from, size);
    }

    @PostMapping("/{id}/comment")
    public CommentDto addComment(@RequestHeader(CustomHeader.USER_ID) Long userId, @PathVariable Long id, @RequestBody CommentDto commentDto) {
        return service.addComment(userId, id, commentDto);
    }

}
