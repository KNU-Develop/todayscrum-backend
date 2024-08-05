package knu.kproject.service;

import jakarta.persistence.EntityNotFoundException;
import knu.kproject.entity.Schedule;
import knu.kproject.global.code.ScheduleType;
import knu.kproject.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;

    public void createSchedule(Schedule schedule) {
        scheduleRepository.save(schedule);
    }
    public void deleteScheduleById(Long scheduleId, ScheduleType type) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new EntityNotFoundException("Schedule not found with ID: " + scheduleId));

        switch (type) {
            case DELETE_THIS:
                scheduleRepository.delete(schedule);
                break;
            case DELETE_BEFORE:
                break;
            case DELETE_ALL:
                break;
            default:
                throw new IllegalArgumentException("Invalid schedule type: " + type);
        }
    }
}
