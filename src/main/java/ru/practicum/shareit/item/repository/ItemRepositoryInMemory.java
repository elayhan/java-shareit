package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryInMemory implements ItemRepository {
    private final Map<Long, Item> itemMap = new HashMap<>();
    private Long id = 0L;

    @Override
    public Item createItem(Item item) {
        if (item.getId() == null) {
            item.setId(++id);
        }
        itemMap.put(id, item);
        return item;
    }

    @Override
    public Item getItem(Long id) {
        if (!itemMap.containsKey(id)) {
            throw new NotFoundException(String.format("Вещь с id: %d не найдена", id));
        }
        return itemMap.get(id);
    }

    @Override
    public List<Item> getAllItems(Long userId) {
        return itemMap.values()
                .stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItem(Long userId, String match) {
        if (match.isEmpty()) {
            return new ArrayList<>();
        }

        return itemMap.values()
                .stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(match) || item.getDescription().toLowerCase().contains(match))
                .collect(Collectors.toList());
    }


    @Override
    public Item updateItem(Long userId, Long id, Item item) {
        Item oldItem = getItem(id);

        if (!oldItem.getOwner().getId().equals(userId)) {
            throw new NotFoundException(String.format("Вещь с id: %d не найдена", id));
        }

        oldItem.setName(item.getName() == null ? oldItem.getName() : item.getName());
        oldItem.setDescription(item.getDescription() == null ? oldItem.getDescription() : item.getDescription());
        oldItem.setAvailable(item.getAvailable() == null ? oldItem.getAvailable() : item.getAvailable());

        return itemMap.get(id);
    }

}
