package knu.kproject.entity.user;

import jakarta.persistence.*;
import knu.kproject.entity.schedule.Schedule;
import knu.kproject.global.schedule.ScheduleInviteState;
import knu.kproject.global.schedule.ScheduleRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @Enumerated(EnumType.STRING)
    private ScheduleRole role;

    private ScheduleInviteState inviteState;


    public UserSchedule(User user, Schedule schedule, ScheduleInviteState state) {
        this.user = user;
        this.schedule = schedule;
        this.inviteState = state;
        schedule.addUserSchedule(this);
        user.addUserSchedule(this);
    }

    public void unUserSchedule() {
        this.getUser().removeUserSchedule(this);
        this.getSchedule().removeUserSchedule(this);
    }
}