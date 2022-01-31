package com.visma.task.service;


import com.visma.task.model.meeting.Attendee;
import com.visma.task.model.meeting.Meeting;
import com.visma.task.view.MeetingFilter;

import java.util.List;

public interface MeetingsService {
    List<Meeting> getAllByParams(MeetingFilter meetingFilter);

    String createMeeting(Meeting newMeeting);

    String deleteMeeting(Long id, String name);

    String addAttendees(Long id, Attendee attendee);

    String removeAttendees(Long id, String name);
}
