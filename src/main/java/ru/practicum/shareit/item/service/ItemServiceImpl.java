package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingOwnerDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotBookedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User user = getUser(userId);


        ItemRequest itemRequest = null;
        if (itemDto.getRequestId() != null) {
            itemRequest = itemRequestRepository.getReferenceById(itemDto.getRequestId());
        }

        itemDto.setOwner(userId);

        Item item = itemMapper.toItem(itemDto, user, itemRequest);
        return itemMapper.toItemDto(repository.save(item));
    }


    @Override
    public ItemDto getItem(Long userId, Long id) {
        ItemDto item = itemMapper.toItemDto(getItemFromRepository(id));
        if (item.getOwner().equals(userId)) {
            setBookings(userId, item);
        }
        setComments(item);
        return item;
    }

    @Override
    public List<ItemDto> getAllItems(Long userId, Integer from, Integer size) {
        getUser(userId);
        Pageable pageable = PageRequest.of(from, size);
        List<ItemDto> items = itemMapper.toListItemDto(repository.findByOwnerId(userId, pageable));
        items.forEach(itemDto -> {
            setBookings(userId, itemDto);
            setComments(itemDto);
        });

        return items;
    }

    private void setComments(ItemDto itemDto) {
        List<Comment> comments = commentRepository.findAllByItemIdOrderByCreatedDesc(itemDto.getId());
        itemDto.setComments(commentMapper.toListCommentDto(comments));
    }

    private void setBookings(Long userId, ItemDto itemDto) {
        Long itemId = itemDto.getId();
        Booking b = bookingRepository.findLastBooking(itemId, userId);
        BookingOwnerDto lastBooking = bookingMapper.toBookingOwnerDto(b);
        BookingOwnerDto nextBooking = bookingMapper.toBookingOwnerDto(bookingRepository.findNextBooking(itemId, userId));

        itemDto.setLastBooking(lastBooking);
        itemDto.setNextBooking(nextBooking);
    }

    @Override
    public List<ItemDto> searchItem(Long userId, String match, Integer from, Integer size) {
        if (match.isEmpty()) {
            return new ArrayList<>();
        }

        Pageable pageable = PageRequest.of(from, size);

        return itemMapper.toListItemDto(repository.findByNameOrDescription(match, pageable));
    }

    @Override
    public ItemDto updateItem(Long userId, Long id, ItemDto itemDto) {
        getUser(userId);
        Item item = repository.findByOwnerIdAndId(userId, id).orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        return itemMapper.toItemDto(repository.save(item));
    }

    @Override
    public CommentDto addComment(Long userId, Long id, CommentDto commentDto) {
        bookingRepository.checkBooked(id, userId).orElseThrow(() -> new NotBookedException("Нельзя оставить отзыв на вещь которую вы еще не брали в аренду"));

        Item item = getItemFromRepository(id);
        User author = getUser(userId);

        commentDto.setCreated(LocalDateTime.now());

        Comment comment = commentMapper.toComment(commentDto, item, author);
        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    private Item getItemFromRepository(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Вещь не найдена"));
    }
}
