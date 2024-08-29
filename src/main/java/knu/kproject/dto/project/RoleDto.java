package knu.kproject.dto.project;

import knu.kproject.global.ROLE;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class RoleDto {
    private Map<Long, ROLE> roles;
}
