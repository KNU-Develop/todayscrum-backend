package knu.kproject.service;

import knu.kproject.dto.workspace.PutworkspaceDto;
import knu.kproject.entity.User;
import knu.kproject.entity.Workspace;
import knu.kproject.repository.UserRepository;
import knu.kproject.repository.WorkspaceRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.util.Optional;

@Service
@Data
public class WorkspaceService {
    @Autowired
    private WorkspaceRepository workspaceRepository;
    @Autowired
    private UserRepository userRepository;
    public Workspace createWorkSpace(PutworkspaceDto workSpaceDto, Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        Workspace workspace = Workspace.builder()
                .ownerId(userId)
                .title(workSpaceDto.getTitle())
                .description(workSpaceDto.getDescription())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

        return workspaceRepository.save(workspace);
    }
}
