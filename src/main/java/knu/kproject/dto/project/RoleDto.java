package knu.kproject.dto.project;

import knu.kproject.global.ROLE;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class RoleDto {
    private UUID projectId;
    private Map<String, ROLE> roles;
}
