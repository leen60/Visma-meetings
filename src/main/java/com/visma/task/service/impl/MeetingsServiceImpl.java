package com.visma.task.service.impl;

import com.visma.task.model.meeting.Attendee;
import com.visma.task.model.meeting.Meeting;
import com.visma.task.exception.ValidationException;
import com.visma.task.repository.MeetingRepository;
import com.visma.task.service.MeetingsService;
import com.visma.task.view.MeetingFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service("meetingsService")
@RequiredArgsConstructor
public class MeetingsServiceImpl implements MeetingsService {
    private final MeetingRepository meetingRepository;

    // ○ Filter by description (if the description is “Jono Java meetas”, searching for
    //java should return this entry)
    //○ Filter by responsible person
    //○ Filter by category
    //○ Filter by type
    //○ Filter by dates (e.g meetings that will happen starting from 2022-01-01 /
    //meetings that will happen between 2022-01-01 and 2022-02-01)
    //○ Filter by the number of attendees (e.g show meetings that have over 10
    //people attending)

    private final MeetingFilter emptyFilters = MeetingFilter.builder().build();

    @Override
    public List<Meeting> getAllByParams(MeetingFilter meetingFilter) {
        List<Meeting> meetings = meetingRepository.findAll();

        if(meetingFilter.equals(this.emptyFilters)) {
            return meetings;
        }

        return meetings.stream().filter(
                x ->
                        (meetingFilter.getDescription() == null ? false : x.getDescription().toLowerCase().contains(meetingFilter.getDescription().toLowerCase()) ) ||
                        (meetingFilter.getResponsiblePerson() == null ? false : x.getResponsiblePerson().equals(meetingFilter.getResponsiblePerson()))
                            || (meetingFilter.getCategory() == null ? false : x.getCategory().equals(meetingFilter.getCategory()))
                            || (meetingFilter.getType() == null ? false : x.getType().equals(meetingFilter.getType()))
                            || (meetingFilter.getStartDate() == null ? false : isWithinRange(x, meetingFilter.getStartDate()))
                            || (meetingFilter.getEndDate() == null ? false : isWithinRange(x, meetingFilter.getEndDate()))
                            || (meetingFilter.getAttendees() == null ? false : (x.getAttendees().size() == meetingFilter.getAttendees()))
        ).collect(Collectors.toList());
    }

    @Override
    public String createMeeting(Meeting newMeeting) {
        meetingRepository.insert(newMeeting);
        return "Meeting created";
    }

    // Rest API endpoint to delete a meeting. Only the person responsible can delete the meeting.
    @Override
    public String deleteMeeting(Long id, String name) {
        Meeting meeting = meetingRepository.findById(id);
        if (meeting.getResponsiblePerson().equals(name)) {
            meetingRepository.deleteById(id);
            return "Success at deleting";
        } else {
            throw new ValidationException("Incorrect name");
        }
    }

    //○ Command should specify who is being added and at what time.
    //○ If a person is already in a meeting which intersects with the one being added, a warning message should be given.
    //○ Prevent the same person from being added twice.

    @Override
    public String addAttendees(Long id, Attendee attendee) {
        List<Meeting> currentMeetings = meetingRepository.findByIdAndAttendee(id, attendee);
        boolean hasIntersects = currentMeetings.stream().anyMatch(x -> isWithinRange(x, attendee.getTime()));

        String returnMessage = hasIntersects ? "Warning attendee has intersects" : "Success";

        Meeting meeting = meetingRepository.findById(id);
        boolean isAlreadyAdded = currentMeetings.contains(meeting);
        if (!isAlreadyAdded) {
            meeting.getAttendees().add(attendee);
            meeting.setAttendees(meeting.getAttendees());
            meetingRepository.updateById(id, meeting);
        }

        return returnMessage;
    }

    // If a person is responsible for the meeting, he can not be removed.
    @Override
    public String removeAttendees(Long id, String name) {
        Meeting meeting = meetingRepository.findById(id);

        boolean isAttendees = meeting.getAttendees().stream().anyMatch(
                x -> x.getName().equals(name)
        );

        if (!isAttendees) throw new ValidationException("Is not attendees");

        boolean isResponsible = meeting.getResponsiblePerson().equals(name);

        if (isResponsible) {
            throw new ValidationException("He can not be removed.");
        } else {
            meeting.getAttendees().removeIf(x -> x.getName().equals(name));
            meetingRepository.updateById(id, meeting);
            return "Removed";
        }
    }

    private boolean isWithinRange(Meeting meeting, LocalDateTime testDate) {
        return !(testDate.isBefore(meeting.getStartDate()) || testDate.isAfter(meeting.getEndDate()));
    }
}
