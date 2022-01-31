package com.visma.task.repository;

import com.visma.task.model.meeting.Attendee;
import com.visma.task.model.meeting.Meeting;

import java.util.List;

public interface MeetingRepository {
    List<Meeting> findAll();

    List<Meeting> findByIdAndAttendee(Long id, Attendee attendee);

    Meeting findById(Long id);

    void insert(Meeting meeting);

    void updateById(Long id, Meeting meeting);

    void deleteById(Long id);
}