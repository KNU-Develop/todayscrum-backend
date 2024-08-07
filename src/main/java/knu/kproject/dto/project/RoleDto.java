package knu.kproject.dto.project;

import knu.kproject.entity.ROLE;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class RoleDto {
    private UUID projectId;
    private Map<String, ROLE> roles;
}
