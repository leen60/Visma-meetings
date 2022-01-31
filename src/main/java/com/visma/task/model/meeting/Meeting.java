package com.visma.task.model.meeting;

import com.visma.task.model.enums.Category;
import com.visma.task.model.enums.Type;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@SuperBuilder
@Data
@AllArgsConstructor
public class Meeting {
    private Long id;
    private String name;
    private List<Attendee> attendees;
    private String responsiblePerson;
    private String description;
    private Category category;
    private Type type;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}