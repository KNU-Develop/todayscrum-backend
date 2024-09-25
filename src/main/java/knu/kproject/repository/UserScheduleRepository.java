package knu.kproject.repository;

import knu.kproject.entity.schedule.Schedule;
import knu.kproject.entity.user.User;
import knu.kproject.entity.user.UserSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserScheduleRepository extends JpaRepository<UserSchedule, Long> {

    Optional<UserSchedule> findByUser_IdAndSchedule_Id(Long userId, Long scheduleId);
    void deleteByUserAndSchedule(User user, Schedule schedule);
    void deleteBySchedule_IdAndUser_Id(Long schedule_id, Long user_id);

    @Query("SELECT us FROM UserSchedule us " +
            "JOIN us.schedule s " +
            "WHERE s.project.id = :projectId " +
            "AND (s.startDate BETWEEN :startDate AND :endDate OR s.endDate BETWEEN :startDate AND :endDate)")
    List<UserSchedule> findByProjectIdAndDateRange(@Param("projectId") UUID projectId,
                                                   @Param("startDate") LocalDateTime startDate,
                                                   @Param("endDate") LocalDateTime endDate);
    List<UserSchedule> findByUser_IdAndSchedule_StartDateBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);
}
