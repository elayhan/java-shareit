package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item createItem(Item item);

    Item getItem(Long id);

    List<Item> getAllItems(Long userId);

    List<Item> searchItem(Long userId, String march);

    Item updateItem(Long userId, Long id, Item item);

}
