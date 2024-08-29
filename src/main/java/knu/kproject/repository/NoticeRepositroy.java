package knu.kproject.repository;

import knu.kproject.entity.notice.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NoticeRepositroy extends JpaRepository<Notice, UUID> {
}
