package com.visma.task.model.meeting;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MeetingsList {
    Long index;
    List<Meeting> meetings;
}