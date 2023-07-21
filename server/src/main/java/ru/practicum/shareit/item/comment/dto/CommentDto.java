package ru.practicum.shareit.item.comment.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentDto {
    private Long id;
    private String text;
    private String authorName;
    private Long itemId;
    private LocalDateTime created;
}
