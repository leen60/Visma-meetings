package com.visma.task.view;

import com.visma.task.model.enums.Category;
import com.visma.task.model.enums.Type;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

// Rest API endpoint to list all the meetings. Add the following parameters to filter the data:
// ○ Filter by description (if the description is “Jono Java meetas”, searching for
//java should return this entry)
//○ Filter by responsible person
//○ Filter by category
//○ Filter by type
//○ Filter by dates (e.g meetings that will happen starting from 2022-01-01 /
//meetings that will happen between 2022-01-01 and 2022-02-01)
//○ Filter by the number of attendees (e.g show meetings that have over 10
//people attending)

@SuperBuilder
@Data
@AllArgsConstructor
public class MeetingFilter {
    private String description;
    private String responsiblePerson;
    private Category category;
    private Type type;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long attendees;
}