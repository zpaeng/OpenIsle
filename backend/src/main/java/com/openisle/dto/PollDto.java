package com.openisle.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class PollDto {
    private String question;
    private List<String> options;
    private Map<Integer, Integer> votes;
    private LocalDateTime endTime;
    private List<AuthorDto> participants;
}
