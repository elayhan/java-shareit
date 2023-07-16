package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static util.CustomHeader.USER_ID;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient client;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader(USER_ID) long userId,
                                                @Valid @RequestBody
                                                ItemRequestDto itemRequestDto) {
        log.info("UserId = {}: Create Request: {}", userId, itemRequestDto);
        return client.createRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnerRequest(@RequestHeader(USER_ID) long userId) {
        log.info("UserID = {}: getOwnedReq", userId);
        return client.getOwnerRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(USER_ID) long userId,
                                                 @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                 @Positive @RequestParam(defaultValue = "20") int size) {
        log.info("UserId={}: getAllReq: from {}; size {}", userId, from, size);
        return client.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(USER_ID) long userId,
                                                 @Positive @PathVariable long requestId) {
        log.info("UserId={}:get request by id = {}", userId, requestId);
        return client.getRequestById(userId, requestId);
    }

}
