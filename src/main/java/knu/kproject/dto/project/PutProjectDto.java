package knu.kproject.dto.project;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class PutProjectDto {
    private String title;
    private String overview;
    private Timestamp startDate;
    private Timestamp endDate;

}
