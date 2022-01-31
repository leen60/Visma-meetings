package com.visma.task.service;

import com.visma.task.VismaMeetingApplication;
import com.visma.task.exception.ValidationException;
import com.visma.task.model.enums.Category;
import com.visma.task.model.enums.Type;
import com.visma.task.model.meeting.Attendee;
import com.visma.task.model.meeting.Meeting;
import com.visma.task.repository.MeetingRepository;
import com.visma.task.util.DatabaseFile;
import com.visma.task.view.MeetingFilter;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertThrows;

@SpringBootTest(classes = VismaMeetingApplication.class)
public class MeetingServiceTest {
    @Autowired
    private Environment env;

    @Autowired
    private MeetingsService meetingsService;

    @Autowired
    private MeetingRepository meetingRepository;

    private final List<Attendee> testAttendeeA = Arrays.asList(
            Attendee.builder()
                    .name("Tomas")
                    .time(LocalDateTime.of(2021, 1, 30, 13, 1, 0, 0))
                    .build(),
            Attendee.builder()
                    .name("Jonas")
                    .time(LocalDateTime.of(2021, 1, 30, 13, 1, 0, 0))
                    .build()
    );

    private final List<Attendee> testAttendeeB = Arrays.asList(
            Attendee.builder()
                    .name("Tomas")
                    .time(LocalDateTime.of(2021, 1, 30, 16, 1, 0, 0))
                    .build(),
            Attendee.builder()
                    .name("Irmantas")
                    .time(LocalDateTime.of(2021, 1, 30, 16, 1, 0, 0))
                    .build()
    );

    private final Meeting testMeetingA = Meeting.builder()
            .name("Meetingas A")
            .attendees(testAttendeeA)
            .responsiblePerson("Audrius")
            .description("Jono Java meetas")
            .category(Category.SHORT)
            .type(Type.LIVE)
            .startDate(LocalDateTime.of(2021, 1, 30, 13, 0, 0, 0))
            .endDate(LocalDateTime.of(2021, 1, 30, 16, 30, 0, 0))
            .build();

    private final Meeting testMeetingB = Meeting.builder()
            .name("Meetingas B")
            .attendees(testAttendeeB)
            .responsiblePerson("Irmantas")
            .description("Tomo C++ meetas")
            .category(Category.HUB)
            .type(Type.INPERSON)
            .startDate(LocalDateTime.of(2021, 1, 30, 16, 0, 0, 0))
            .endDate(LocalDateTime.of(2021, 1, 30, 18, 0, 0, 0))
            .build();

    public MeetingServiceTest() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
        String setupJSON = "{\n  \"data\" : {\n    \"index\" : 1,\n    \"meetings\" : []\n  }\n}";
        new DatabaseFile(env.getProperty("test.datasource.file-path")).write(setupJSON);
    }

    @AfterEach
    public void tearDown() {
    }

    private void insetMeetings(){
        meetingRepository.insert(testMeetingA);
        meetingRepository.insert(testMeetingB);
    }

    @Test
    public void getAllByDescription() {
        this.insetMeetings();

        List<Meeting> result = meetingsService.getAllByParams(MeetingFilter.builder().description("java").build());

        Assertions.assertEquals(result.size(), 1);
        Assertions.assertEquals(testMeetingA, result.get(0));
    }

    @Test
    public void getAllByResponsiblePerson() {
        this.insetMeetings();

        List<Meeting> result = meetingsService.getAllByParams(MeetingFilter.builder().responsiblePerson("Irmantas").build());

        Assertions.assertEquals(result.size(), 1);
        Assertions.assertEquals(testMeetingB, result.get(0));
    }

    @Test
    public void getAllByCategory() {
        this.insetMeetings();

        List<Meeting> result = meetingsService.getAllByParams(MeetingFilter.builder().category(Category.HUB).build());

        Assertions.assertEquals(result.size(), 1);
        Assertions.assertEquals(testMeetingB, result.get(0));
    }

    @Test
    public void getAllByType() {
        this.insetMeetings();

        List<Meeting> result = meetingsService.getAllByParams(MeetingFilter.builder().type(Type.LIVE).build());

        Assertions.assertEquals(result.size(), 1);
        Assertions.assertEquals(testMeetingA, result.get(0));
    }

    @Test
    public void getAllByStartDate() {
        this.insetMeetings();

        List<Meeting> result = meetingsService.getAllByParams(MeetingFilter.builder().startDate((LocalDateTime.of(2021, 1, 30, 17, 30, 0, 0))).build());

        Assertions.assertEquals(result.size(), 1);
        Assertions.assertEquals(testMeetingB, result.get(0));
    }

    @Test
    public void getAllByEndDate() {
        this.insetMeetings();

        List<Meeting> result = meetingsService.getAllByParams(MeetingFilter.builder().startDate((LocalDateTime.of(2021, 1, 30, 14, 30, 0, 0))).build());

        Assertions.assertEquals(result.size(), 1);
        Assertions.assertEquals(testMeetingA, result.get(0));
    }

    @Test
    public void getAllByAttendees() {
        this.insetMeetings();

        List<Meeting> result = meetingsService.getAllByParams(MeetingFilter.builder().attendees(2L).build());

        Assertions.assertEquals(result.size(), 2);
        Assertions.assertEquals(testMeetingA, result.get(0));
        Assertions.assertEquals(testMeetingB, result.get(1));
    }

    @Test
    public void deleteMeetingByNameResponsible() {
        this.insetMeetings();

        meetingsService.deleteMeeting(2L, "Audrius");

        List<Meeting> result = meetingRepository.findAll();
        Assertions.assertEquals(result.size(), 1);
        Assertions.assertEquals(result.get(0), testMeetingB);
    }

    @Test
    public void deleteMeetingByNameNotResponsible() {
        this.insetMeetings();

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            meetingsService.deleteMeeting(2L, "Irmantas");
        });

        Assertions.assertEquals(exception.getMessage(), "Incorrect name");
    }

    @Test
    public void createMeeting() {
        String result = meetingsService.createMeeting(testMeetingA);
        Meeting meeting = meetingRepository.findById(2L);

        Assertions.assertEquals(result, "Meeting created");
        Assertions.assertEquals(meeting, testMeetingA);
    }


    @Test
    public void addAttendee() {
        this.insetMeetings();

        Attendee testAttendee = Attendee.builder()
                .name("Aurimas")
                .time(LocalDateTime.of(2021, 1, 30, 12, 0, 0, 0))
                .build();

        meetingsService.addAttendees(2L, testAttendee);

        List<Meeting> result = meetingRepository.findByIdAndAttendee(2L, testAttendee);
        Assertions.assertEquals(result.size(), 1);
        Assertions.assertTrue(result.get(0).getAttendees().contains(testAttendee));
    }

    @Test
    public void addAttendeeWithWarning() {
        this.insetMeetings();

        Attendee testAttendee = Attendee.builder()
                .name("Irmantas")
                .time(LocalDateTime.of(2021, 1, 30, 16, 1, 0, 0))
                .build();


        String result = meetingsService.addAttendees(3L, testAttendee);
        Assertions.assertEquals(result, "Warning attendee has intersects");
    }

    @Test
    public void removeAttendeeNotResponsible() {
        this.insetMeetings();

        meetingsService.removeAttendees(2L, "Tomas");
        Meeting result = meetingRepository.findById(2L);
        Assertions.assertEquals(result.getAttendees().size(), 1);

        Assertions.assertFalse((result.getAttendees().contains(testAttendeeA.get(0))));
    }

    @Test
    public void removeAttendeeResponsible() {
        this.insetMeetings();

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            meetingsService.removeAttendees(3L, "Irmantas");
        });

        Assertions.assertEquals(exception.getMessage(), "He can not be removed.");
    }
}
