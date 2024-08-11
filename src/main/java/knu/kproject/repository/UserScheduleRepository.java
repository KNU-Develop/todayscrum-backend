package knu.kproject.repository;

import knu.kproject.entity.schedule.Schedule;
import knu.kproject.entity.user.User;
import knu.kproject.entity.user.UserSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserScheduleRepository extends JpaRepository<UserSchedule, Long> {
    @Query("SELECT us.schedule " +
            "FROM UserSchedule us " +
            "WHERE us.user.id = :userId AND us.schedule.id = :scheduleId")
    Optional<Schedule> findScheduleByUserIdAndScheduleId(
            @Param("userId") Long userId,
            @Param("scheduleId") Long scheduleId);
//    @Query("SELECT us " +
//            "FROM UserSchedule us " +
//            "WHERE us.user.id = :userId AND us.schedule.id = :scheduleId")
//    Optional<UserSchedule> findUserScheduleByUserIdAndScheduleId(
//            @Param("userId") Long userId,
//            @Param("scheduleId") Long scheduleId);

    Optional<UserSchedule> findUserScheduleByUser_IdAndSchedule_Id(Long userId, Long scheduleId);

    void deleteUserScheduleByUserAndSchedule(User user, Schedule schedule);
}
