package com.visma.task.controller;

import com.visma.task.model.meeting.Attendee;
import com.visma.task.model.meeting.Meeting;
import com.visma.task.service.MeetingsService;
import com.visma.task.view.MeetingFilter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/v1/meetings")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingsService meetingsService;

    /*Rest API endpoint to list all the meetings.*/
    @GetMapping
    public List<Meeting> getAllByParams(MeetingFilter meetingFilter) {
        return meetingsService.getAllByParams(meetingFilter);
    }

    /* Rest API endpoint to create a new meeting.*/
    @PostMapping
    public ResponseEntity<String> create(@RequestBody @NotNull @Valid Meeting newMeeting) {
        return ResponseEntity.ok(meetingsService.createMeeting(newMeeting));
    }

    /*Rest API endpoint to add a person to the meeting*/
    @PostMapping("/{id}")
    public ResponseEntity<String> addAttendee(@PathVariable @NonNull @Positive Long id, @RequestBody @NotNull @Valid Attendee attendee) {
        return ResponseEntity.ok(meetingsService.addAttendees(id, attendee));
    }

    /*Rest API endpoint to remove a person from the meeting.*/
    @PostMapping("/{id}/{name}")
    public ResponseEntity<String> removeAttendee(@PathVariable @NonNull @Positive Long id, @PathVariable @NonNull @Positive String name) {
        return ResponseEntity.ok(meetingsService.removeAttendees(id, name));
    }

    /*Rest API endpoint to delete a meeting.*/
    @DeleteMapping("/{id}/{name}")
    public ResponseEntity<String> delete(@PathVariable @NonNull @Positive Long id, @PathVariable @NonNull @Positive String name) {
        return ResponseEntity.ok(meetingsService.deleteMeeting(id, name));
    }

}
