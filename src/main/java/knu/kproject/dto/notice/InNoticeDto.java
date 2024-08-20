package knu.kproject.dto.notice;


import knu.kproject.global.CHOICE;
import lombok.Data;

import java.util.UUID;

@Data
public class InNoticeDto {
    private UUID noticeId;
    private CHOICE choice;
}
