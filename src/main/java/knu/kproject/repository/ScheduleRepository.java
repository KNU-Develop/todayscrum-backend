package knu.kproject.repository;

import knu.kproject.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    @Query("SELECT s " +
            "FROM Schedule s JOIN s.userSchedules us " +
            "WHERE us.user.id = :userId AND s.startDate >= :startDate AND s.endDate <= :endDate")
    Optional<List<Schedule>> findScheduleList(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT s " +
            "FROM Schedule s JOIN s.userSchedules us " +
            "WHERE us.user.id = :userId AND s.id = :scheduleId")
    Optional<Schedule> findScheduleByUserIdAndScheduleId(
            @Param("userId") Long userId,
            @Param("scheduleId") Long scheduleId);
}