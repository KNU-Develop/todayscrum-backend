package knu.kproject.dto.project;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class InviteDto {
    private UUID projectId;
    private List<String> userEmails;
}
