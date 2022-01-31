package com.visma.task.repository.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.visma.task.exception.DatasourceException;
import com.visma.task.exception.ValidationException;
import com.visma.task.model.BaseEntity;
import com.visma.task.model.meeting.Attendee;
import com.visma.task.model.meeting.Meeting;
import com.visma.task.model.meeting.MeetingsList;
import com.visma.task.repository.MeetingRepository;
import com.visma.task.util.DatabaseFile;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Component("meetingRepoImpl")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class MeetingRepoImpl implements MeetingRepository {
    ReentrantLock lock;
    DatabaseFile meetingFile;
    ObjectMapper objectMapper;

    MeetingRepoImpl(Environment env) {
        // for separate testing db
        if (env.getProperty("test.datasource.file-path") != null) {
            meetingFile = new DatabaseFile(env.getProperty("test.datasource.file-path"));
        } else {
            meetingFile = new DatabaseFile(env.getProperty("datasource.file-path"));
        }

        lock = new ReentrantLock();

        objectMapper = JsonMapper.builder()
                .addModule(new ParameterNamesModule())
                .addModule(new Jdk8Module())
                .addModule(new JavaTimeModule())
                .build();
    }

    @Override
    public List<Meeting> findAll() {
        lock.lock();
        try {
            BaseEntity<MeetingsList> date = this.read();
            return date.getData().getMeetings();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<Meeting> findByIdAndAttendee(Long id, Attendee attendee) {
        lock.lock();
        try {
            BaseEntity<MeetingsList> date = this.read();
            List<Meeting> allMeetings = date.getData().getMeetings();
            return allMeetings.stream().filter(x -> x.getAttendees().stream().anyMatch(y -> y.getName().equals(attendee.getName()))).collect(Collectors.toList());
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Meeting findById(Long id) {
        lock.lock();
        try {
            BaseEntity<MeetingsList> date = this.read();
            return date.getData().getMeetings().stream()
                    .filter(x -> x.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new ValidationException(String.format("Meeting %s not found", id)));
        } finally {
            lock.unlock();
        }
    }


    @Override
    public void insert(Meeting meeting) {
        lock.lock();
        try {
            BaseEntity<MeetingsList> date = this.read();

            MeetingsList meetings = date.getData();
            Long index = meetings.getIndex();

            meeting.setId(index + 1L);

            meetings.getMeetings().add(meeting);
            meetings.setIndex(index + 1L);

            this.write(date);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void updateById(Long id, Meeting meeting) {
        lock.lock();
        try {
            BaseEntity<MeetingsList> date = this.read();
            Meeting oldMeeting = date.getData().getMeetings().stream()
                    .filter(x -> x.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new ValidationException(String.format("Meeting %s not found", id)));

            oldMeeting.setName(meeting.getName());
            oldMeeting.setAttendees(meeting.getAttendees());
            oldMeeting.setResponsiblePerson(meeting.getResponsiblePerson());
            oldMeeting.setDescription(meeting.getDescription());
            oldMeeting.setCategory(meeting.getCategory());
            oldMeeting.setType(meeting.getType());
            oldMeeting.setStartDate(meeting.getStartDate());
            oldMeeting.setEndDate(meeting.getEndDate());

            this.write(date);

        } finally {
            lock.unlock();
        }
    }

    @Override
    public void deleteById(Long id) {
        lock.lock();
        try {
            BaseEntity<MeetingsList> date = this.read();
            date.getData().getMeetings().removeIf(x -> x.getId().equals(id));
            this.write(date);

        } finally {
            lock.unlock();
        }
    }

    private BaseEntity<MeetingsList> read() {
        BaseEntity<MeetingsList> date = null;
        try {
            String json = meetingFile.read();
            date = objectMapper.readValue(json, new TypeReference<BaseEntity<MeetingsList>>() {
            });
        } catch (JsonProcessingException e) {
            throw new DatasourceException(e);
        }
        return date;
    }

    private void write(BaseEntity<MeetingsList> date) {
        try {
            String jsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(date);
            meetingFile.write(jsonString);
        } catch (JsonProcessingException e) {
            throw new DatasourceException(e);
        }
    }
}
