package com.visma.task.model.meeting;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@SuperBuilder
@Data
@AllArgsConstructor
public class Attendee {
    private String name;
    private LocalDateTime time;
}