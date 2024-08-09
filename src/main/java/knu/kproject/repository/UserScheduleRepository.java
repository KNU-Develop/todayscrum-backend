package knu.kproject.repository;

import knu.kproject.entity.Schedule;
import knu.kproject.entity.UserSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserScheduleRepository extends JpaRepository<UserSchedule, Long> {
    @Query("SELECT us.schedule " +
            "FROM UserSchedule us " +
            "WHERE us.user.id = :userId AND us.schedule.id = :scheduleId")
    Optional<Schedule> findScheduleByUserIdAndScheduleId(
            @Param("userId") Long userId,
            @Param("scheduleId") Long scheduleId);
    List<UserSchedule> findByUserId(Long userId);
}
