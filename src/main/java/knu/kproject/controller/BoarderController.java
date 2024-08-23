package knu.kproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping("project/{projectId}/board")
@RequiredArgsConstructor
public class BoarderController {
    private final BoardService boardService;

    @Operation(summary = "보드 생성", description = "보드 생성 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "보드 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 request"),
            @ApiResponse(responseCode = "401", description = "Authorizeation 오류"),
            @ApiResponse(responseCode = "403", description = "권한 오류"),
            @ApiResponse(responseCode = "404", description = "프로젝트가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/")
    public ResponseEntity<Api_Response<Object>> createBoard(@AuthenticationPrincipal Long token, @PathVariable UUID projectId, @RequestBody InputBoardDto boardDto) {
        UUID boarderId = boardService.createBoard(token, projectId, boardDto);
        Map<String, UUID> boarderMap = new HashMap<>();
        boarderMap.put("BoarderId", boarderId);

        return ApiResponseUtil.createSuccessResponse(
                SuccessCode.INSERT_SUCCESS.getMessage(),
                boarderMap
        );
    }

    @Operation(summary = "board 조회", description = "보드 조회 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "보드 조회 성공"),
            @ApiResponse(responseCode = "401", description = "Authorizeation 오류"),
            @ApiResponse(responseCode = "403", description = "권한 오류"),
            @ApiResponse(responseCode = "404", description = "보드가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{boardId}")
    public ResponseEntity<Api_Response<Object>> findByBoard(@AuthenticationPrincipal Long token, @PathVariable UUID boardId) {
        BoardDto boardDto = boardService.findByBoard(token, boardId);

        return ApiResponseUtil.createSuccessResponse(
                SuccessCode.SELECT_SUCCESS.getMessage(),
                boardDto
        );
    }

    @Operation(summary = "특정 프로젝트 내 모든 board 조회", description = "프로젝트 내 모든 보드 조회 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "보드 조회 성공"),
            @ApiResponse(responseCode = "401", description = "Authorizeation 오류"),
            @ApiResponse(responseCode = "403", description = "권한 오류"),
            @ApiResponse(responseCode = "404", description = "프로젝트가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/")
    public ResponseEntity<Api_Response<Object>> findByAllBoard(@AuthenticationPrincipal Long token, @PathVariable UUID projectId) {
        List<BoardDto> boardDto = boardService.findByAllBoard(token, projectId);

        return ApiResponseUtil.createSuccessResponse(
                SuccessCode.SELECT_SUCCESS.getMessage(),
                boardDto
        );
    }

    @Operation(summary = "board 수정", description = "보드 수정 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "보드 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 request"),
            @ApiResponse(responseCode = "401", description = "Authorizeation 오류"),
            @ApiResponse(responseCode = "403", description = "권한 오류"),
            @ApiResponse(responseCode = "404", description = "보드가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping("/{boardId}")
    public ResponseEntity<Api_Response<Object>> updateBoard(@AuthenticationPrincipal Long token, @PathVariable UUID boardId, @RequestBody InputBoardDto input) {
        boardService.updateBoard(token, boardId, input);

        return ApiResponseUtil.createSuccessResponse(
                SuccessCode.UPDATE_SUCCESS.getMessage()
        );
    }

    @Operation(summary = "board 삭제", description = "보드 삭제 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "보드 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "Authorizeation 오류"),
            @ApiResponse(responseCode = "403", description = "권한 오류"),
            @ApiResponse(responseCode = "404", description = "보드가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/{boardId}")
    public ResponseEntity<Api_Response<Object>> deleteBoard(@AuthenticationPrincipal Long token, @PathVariable UUID boardId) {
        boardService.deleteBoard(token, boardId);

        return ApiResponseUtil.createSuccessResponse(
                SuccessCode.DELETE_SUCCESS.getMessage()
        );
    }
}
