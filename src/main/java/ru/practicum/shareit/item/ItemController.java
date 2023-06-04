package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @Valid @RequestBody ItemDto itemDto) {
        return service.createItem(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long id,
                              @RequestBody ItemDto itemDto) {
        return service.updateItem(userId, id, itemDto);
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable Long id) {
        return service.getItem(id);
    }

    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.getAllItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam String text) {
        return service.searchItem(userId, text.toLowerCase());
    }

}
