package knu.kproject.dto.board;

import knu.kproject.entity.Master;
import knu.kproject.entity.User;
import knu.kproject.repository.UserRepository;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;


@Data
@Builder
public class MasterDto {
    private String name;
    private String email;
}
