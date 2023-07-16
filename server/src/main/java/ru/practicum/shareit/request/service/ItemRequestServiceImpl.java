package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository repository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;


    private final ItemRequestMapper mapper;
    private final ItemMapper itemMapper;

    @Override
    public ItemRequestDto createRequest(Long userId, ItemRequestDto requestDto) {
        User requestor = getUser(userId);

        ItemRequest itemRequest = mapper.toItemRequest(requestDto);
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.now());

        return mapper.toItemRequestDto(repository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getOwnerRequests(Long userId) {
        getUser(userId);
        List<ItemRequestDto> itemRequestDtoList = mapper.toListItemRequestDto(repository
                .findAllByRequestorIdOrderByCreatedDesc(userId));

        itemRequestDtoList.forEach(this::setItems);

        return itemRequestDtoList;
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size) {
        getUser(userId);

        Pageable pageable = PageRequest.of(from, size);
        List<ItemRequestDto> itemRequestDtoList = mapper.toListItemRequestDto(repository
                .findAllByRequestorIdNot(userId, pageable));

        itemRequestDtoList.forEach(this::setItems);

        return itemRequestDtoList;
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        getUser(userId);

        ItemRequest itemRequest = repository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с таким id не найден"));

        ItemRequestDto itemRequestDto = mapper.toItemRequestDto(itemRequest);

        setItems(itemRequestDto);

        return itemRequestDto;
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден."));
    }

    private void setItems(ItemRequestDto itemRequestDto) {
        itemRequestDto.setItems(itemMapper.toListItemDto(itemRepository.findAllByRequestId(itemRequestDto.getId())));
    }

}
