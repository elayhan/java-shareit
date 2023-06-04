package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserRepository userRepository;

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User user = userRepository.getUser(userId);

        itemDto.setOwner(user);

        Item item = ItemMapper.toItem(itemDto);
        return ItemMapper.toItemDto(repository.createItem(item));
    }

    @Override
    public ItemDto getItem(Long id) {
        return ItemMapper.toItemDto(repository.getItem(id));
    }

    @Override
    public List<ItemDto> getAllItems(Long userId) {
        List<ItemDto> result = new ArrayList<>();
        repository.getAllItems(userId).forEach(item -> result.add(ItemMapper.toItemDto(item)));
        return result;
    }

    @Override
    public List<ItemDto> searchItem(Long userId, String match) {
        List<ItemDto> result = new ArrayList<>();
        repository.searchItem(userId, match).forEach(item -> result.add(ItemMapper.toItemDto(item)));
        return result;
    }

    @Override
    public ItemDto updateItem(Long userId, Long id, ItemDto itemDto) {
        userRepository.getUser(userId);
        Item item = ItemMapper.toItem(itemDto);
        return ItemMapper.toItemDto(repository.updateItem(userId, id, item));
    }
}
