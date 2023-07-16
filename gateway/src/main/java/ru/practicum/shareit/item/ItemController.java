package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import static util.CustomHeader.USER_ID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private final ItemClient client;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(USER_ID) long userId,
                                             @Valid @RequestBody ItemDto itemDto) {
        log.info("UserId = {}: Create item {}", userId, itemDto.toString());
        return client.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(USER_ID) long userId,
                                             @PathVariable long itemId,
                                             @RequestBody ItemDto itemDto) {
        log.info("UserID: {} - Update itemId: {} from item: {}", userId, itemId, itemDto.toString());
        return client.updateItem(userId, itemId, itemDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader(USER_ID) long userId,
                                              @RequestParam(defaultValue = "0") @Min(0) int from,
                                              @RequestParam(defaultValue = "20") @Min(1) int size) {
        log.info("UserID = {}: get all Items", userId);
        return client.getAllItems(userId, from, size);
    }

    @GetMapping("{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(USER_ID) long userId,
                                              @PathVariable long itemId) {
        log.info("UserID = {}: get Item by ID = {}", userId, itemId);
        return client.getItemById(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestHeader(USER_ID) long userId,
                                             @RequestParam String text,
                                             @RequestParam(defaultValue = "0") @Min(0) int from,
                                             @RequestParam(defaultValue = "20") @Min(1) int size) {
        log.info("UserID = {} search - {}", userId, text);
        return client.searchItem(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(USER_ID) long userId,
                                             @PathVariable long itemId,
                                             @Valid @RequestBody CommentDto commentDto) {
        log.info("UserID = {} add comment {} to ItemID = {}", userId, commentDto, itemId);
        return client.addComment(userId, itemId, commentDto);
    }


}
