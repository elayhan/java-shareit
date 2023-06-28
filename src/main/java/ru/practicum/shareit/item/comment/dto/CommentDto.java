package ru.practicum.shareit.item.comment.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
public class CommentDto {
    private Long id;
    @NotBlank
    private String text;
    private String authorName;
    private Long itemId;
    private LocalDateTime created;
}
