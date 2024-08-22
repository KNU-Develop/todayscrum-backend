package knu.kproject.entity.schedule;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import knu.kproject.dto.schedule.ScheduleReqDto;
import knu.kproject.entity.user.UserSchedule;
import knu.kproject.global.schedule.ScheduleVisible;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    @Enumerated(EnumType.STRING)
    private ScheduleVisible visible;
    private Long projectId;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<UserSchedule> userSchedules = new ArrayList<>();

    /*
        생성자
     */
    public Schedule(ScheduleReqDto scheduleRequestDto) {
        this.title = scheduleRequestDto.getTitle();
        this.content = scheduleRequestDto.getContent();
        this.startDate = scheduleRequestDto.getStartDate();
        this.endDate = scheduleRequestDto.getEndDate();
        this.visible = scheduleRequestDto.getVisible();
        this.projectId = scheduleRequestDto.getProjectId();
    }

    /*
        연관 관계 메서드
     */
    public void addUserSchedule(UserSchedule userSchedule) {
        userSchedules.add(userSchedule);
        userSchedule.setSchedule(this);
    }

    public void removeUserSchedule(UserSchedule userSchedule) {
        userSchedules.remove(userSchedule);
        userSchedule.setSchedule(null);
    }
    public void updateSchedule(ScheduleReqDto scheduleRequestDto) {
        this.title = scheduleRequestDto.getTitle();
        this.content = scheduleRequestDto.getContent();
        this.startDate = scheduleRequestDto.getStartDate();
        this.endDate = scheduleRequestDto.getEndDate();
        this.visible = scheduleRequestDto.getVisible();
        this.projectId = scheduleRequestDto.getProjectId();
    }
}


