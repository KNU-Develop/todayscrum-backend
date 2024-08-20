package knu.kproject.dto.UserDto;

import knu.kproject.entity.user.User;
import knu.kproject.entity.user.UserSchedule;
import knu.kproject.global.schedule.ScheduleInviteState;

public class UserHeadDto {
    private Long id;
    private String name;
    private String email;
    private ScheduleInviteState scheduleInviteState;

    public UserHeadDto(UserSchedule userSchedule) {
        User user = userSchedule.getUser();
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.scheduleInviteState = userSchedule.getInviteState();
    }
}
