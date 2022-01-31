package com.visma.task.repository;

import com.visma.task.VismaMeetingApplication;
import com.visma.task.exception.ValidationException;
import com.visma.task.model.enums.Category;
import com.visma.task.model.enums.Type;
import com.visma.task.model.meeting.Attendee;
import com.visma.task.model.meeting.Meeting;
import com.visma.task.util.DatabaseFile;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertThrows;

@SpringBootTest(classes = VismaMeetingApplication.class)
public class MeetingRepositoryTest {
    @Autowired
    private Environment env;

    @Autowired
    private MeetingRepository meetingRepository;

    private final List<Attendee> testAttendee = Arrays.asList(
            Attendee.builder()
                    .name("Tomas")
                    .time(LocalDateTime.of(2021, 1, 30, 12, 0, 0, 0))
                    .build(),
            Attendee.builder()
                    .name("Jonas")
                    .time(LocalDateTime.of(2021, 1, 30, 12, 0, 0, 0))
                    .build()
    );
    private final Meeting testMeeting = Meeting.builder()
            .id(1L)
            .name("Meetingas 1")
            .attendees(testAttendee)
            .responsiblePerson("Jonas")
            .description("test1")
            .category(Category.SHORT)
            .type(Type.LIVE)
            .startDate(LocalDateTime.of(2021, 1, 30, 13, 0, 0, 0))
            .endDate(LocalDateTime.of(2021, 1, 30, 13, 0, 0, 0))
            .build();

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

    @Test
    public void findById() {
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            meetingRepository.findById(1L);
        });

        Assertions.assertEquals(exception.getMessage(), "Meeting 1 not found");
    }

    @Test
    public void insert() {
        meetingRepository.insert(testMeeting);

        Meeting result = meetingRepository.findById(testMeeting.getId());
        Assertions.assertEquals(testMeeting, result);
    }

    @Test
    public void findAll() {
        meetingRepository.insert(testMeeting);
        meetingRepository.insert(testMeeting);
        meetingRepository.insert(testMeeting);

        List<Meeting> result = meetingRepository.findAll();
        Assertions.assertEquals(result.size(), 3L);

        Long idx = 1L;

        for (Meeting r : result){
            Assertions.assertEquals(r, Meeting.builder()
                    .id(++idx)
                    .name("Meetingas 1")
                    .attendees(testAttendee)
                    .responsiblePerson("Jonas")
                    .description("test1")
                    .category(Category.SHORT)
                    .type(Type.LIVE)
                    .startDate(LocalDateTime.of(2021, 1, 30, 13, 0, 0, 0))
                    .endDate(LocalDateTime.of(2021, 1, 30, 13, 0, 0, 0))
                    .build());
        }
    }

    @Test
    public void findByIdAndAttendee() {
        meetingRepository.insert(testMeeting);

        List<Meeting> result = meetingRepository.findByIdAndAttendee(testMeeting.getId(), testAttendee.get(0));
        Assertions.assertEquals(result.size(), 1);
        Assertions.assertEquals(testMeeting, result.get(0));

        result = meetingRepository.findByIdAndAttendee(testMeeting.getId(), testAttendee.get(1));
        Assertions.assertEquals(result.size(), 1);
        Assertions.assertEquals(testMeeting, result.get(0));
    }

    @Test
    public void updateById() {
        meetingRepository.insert(testMeeting);

        testMeeting.setName("Meetingas test");
        meetingRepository.updateById( testMeeting.getId(), testMeeting);

        Meeting result = meetingRepository.findById(testMeeting.getId());

        Assertions.assertEquals(result.getName(), "Meetingas test");
    }

    @Test
    public void deleteById() {
        meetingRepository.insert(testMeeting);
        meetingRepository.deleteById(testMeeting.getId());

        List<Meeting> result = meetingRepository.findAll();
        Assertions.assertEquals(result.size(), 0);
    }

    @AfterEach
    public void tearDown() {}
}
