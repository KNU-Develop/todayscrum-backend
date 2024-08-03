package knu.kproject.repository;

import knu.kproject.entity.Schedule;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    @Query("select s from schedule as s" +
            "where e.start_date <= :endDate and e.end_date >= :startDate")
    List<Schedule> findESchedulesBetweenDates(@Param("startDate")LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate);
}
