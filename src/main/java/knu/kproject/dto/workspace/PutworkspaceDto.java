package knu.kproject.dto.workspace;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PutworkspaceDto {
    private Long id;
    private Long ownerId;
    private String title;
    private String description;
    private Timestamp createdAt;
}
