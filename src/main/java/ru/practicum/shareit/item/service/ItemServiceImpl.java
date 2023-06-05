package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User user = userRepository.getUser(userId);

        itemDto.setOwner(userId);

        Item item = itemMapper.toItem(itemDto, user);
        return itemMapper.toItemDto(repository.createItem(item));
    }

    @Override
    public ItemDto getItem(Long id) {
        return itemMapper.toItemDto(repository.getItem(id));
    }

    @Override
    public List<ItemDto> getAllItems(Long userId) {
        return itemMapper.toListItemDto(repository.getAllItems(userId));
    }

    @Override
    public List<ItemDto> searchItem(Long userId, String match) {
        return itemMapper.toListItemDto(repository.searchItem(userId, match));
    }

    @Override
    public ItemDto updateItem(Long userId, Long id, ItemDto itemDto) {
        User user = userRepository.getUser(userId);
        Item item = itemMapper.toItem(itemDto, user);
        return itemMapper.toItemDto(repository.updateItem(userId, id, item));
    }
}
