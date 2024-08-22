package knu.kproject.dto.project;


import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.sql.Timestamp;

@Data
public class PutProjectDto {
    private String title;
    private String overview;
    private Timestamp startDate;
    private Timestamp endDate;
    private String color;
}
