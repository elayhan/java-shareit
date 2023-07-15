package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.util.CustomHeader;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService service;

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader(CustomHeader.USER_ID) Long userId,
                                        @Valid @RequestBody ItemRequestDto requestDto) {
        return service.createRequest(userId, requestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getOwnerRequests(@RequestHeader(CustomHeader.USER_ID) Long userId) {
        return service.getOwnerRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader(CustomHeader.USER_ID) Long userId,
                                               @RequestParam(defaultValue = "0") Integer from,
                                               @RequestParam(defaultValue = "10") Integer size) {
        return service.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader(CustomHeader.USER_ID) Long userId,
                                         @PathVariable Long requestId) {
        return service.getRequestById(userId, requestId);
    }


}
