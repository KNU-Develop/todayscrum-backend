package knu.kproject.repository;

import knu.kproject.entity.schedule.Schedule;
import knu.kproject.entity.user.User;
import knu.kproject.entity.user.UserSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserScheduleRepository extends JpaRepository<UserSchedule, Long> {

    Optional<UserSchedule> findUserScheduleByUser_IdAndSchedule_Id(Long userId, Long scheduleId);
    void deleteByUserAndSchedule(User user, Schedule schedule);
    void deleteBySchedule_IdAndUser_Id(Long schedule_id, Long user_id);
}
