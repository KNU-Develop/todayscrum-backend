package knu.kproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.websocket.server.PathParam;
import knu.kproject.dto.board.BoardDto;
import knu.kproject.dto.board.InputBoardDto;
import knu.kproject.global.code.Api_Response;
import knu.kproject.global.code.SuccessCode;
import knu.kproject.service.BoardService;
import knu.kproject.util.ApiResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Board API", description = "Board API 명세서 입니다.")
@RestController
@RequestMapping("/workspace/project/board")
@RequiredArgsConstructor
public class BoarderController {
    private final BoardService boardService;

    @Operation(summary = "board 생성", description = "보드 생성 API 입니다.")
    @Parameter(name = "key", description = "projectId의 값 입니다.")
    @PostMapping("")
    public ResponseEntity<Api_Response<Object>> createBoard(@AuthenticationPrincipal Long token, @RequestParam UUID key, @RequestBody InputBoardDto boardDto) {
        UUID boarderId = boardService.createBoard(token, key, boardDto);
        Map<String, UUID> boarderMap = new HashMap<>();
        boarderMap.put("BoarderId", boarderId);

        return ApiResponseUtil.createResponse(
                SuccessCode.INSERT_SUCCESS.getStatus(),
                SuccessCode.INSERT_SUCCESS.getMessage(),
                boarderMap
        );
    }

    @Operation(summary = "board 조회", description = "보드 조회 API 입니다.")
    @GetMapping("/{boardId}")
    public ResponseEntity<Api_Response<Object>> findByBoard(@AuthenticationPrincipal Long token, @PathVariable UUID boardId) {
        BoardDto boardDto = boardService.findByBoard(token, boardId);

        return ApiResponseUtil.createResponse(
                SuccessCode.SELECT_SUCCESS.getStatus(),
                SuccessCode.SELECT_SUCCESS.getMessage(),
                boardDto
        );
    }

    @Operation(summary = "특정 프로젝트 내 모든 board 조회", description = "프로젝트 내 모든 보드 조회 API 입니다.")
    @Parameter(name = "key", description = "projectId의 값 입니다.")
    @GetMapping("")
    public ResponseEntity<Api_Response<Object>> findByAllBoard(@AuthenticationPrincipal Long token, @RequestParam UUID key) {
        List<BoardDto> boardDto = boardService.findByAllBoard(token, key);

        return ApiResponseUtil.createResponse(
                SuccessCode.SELECT_SUCCESS.getStatus(),
                SuccessCode.SELECT_SUCCESS.getMessage(),
                boardDto
        );
    }

    @Operation(summary = "board 수정", description = "보드 수정 API 입니다.")
    @PutMapping("{boardId}")
    public ResponseEntity<Api_Response<Object>> updateBoard(@AuthenticationPrincipal Long token, @PathVariable UUID boardId, @RequestBody InputBoardDto input) {
        boardService.updateBoard(token, boardId, input);

        return ApiResponseUtil.createSuccessResponse(
                SuccessCode.UPDATE_SUCCESS.getMessage()
        );
    }

    @Operation(summary = "board 삭제", description = "보드 삭제 API 입니다.")
    @DeleteMapping("{boardId}")
    public ResponseEntity<Api_Response<Object>> deleteBoard(@AuthenticationPrincipal Long token, @PathVariable UUID boardId) {
        boardService.deleteBoard(token, boardId);

        return ApiResponseUtil.createSuccessResponse(
                SuccessCode.DELETE_SUCCESS.getMessage()
        );
    }
}
