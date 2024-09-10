package knu.kproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import knu.kproject.dto.board.BoardDto;
import knu.kproject.dto.comment.CommentDto;
import knu.kproject.dto.comment.InputCommentDto;
import knu.kproject.entity.comment.Comment;
import knu.kproject.global.code.Api_Response;
import knu.kproject.global.code.SuccessCode;
import knu.kproject.service.CommentService;
import knu.kproject.util.ApiResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Comment API", description = "보드 내 댓글 api 입니다.")
@RequestMapping("project/{projectId}/board/{boardId}/comment")
public class CommentController {
    private final CommentService commentService;

    @Operation(summary = "comment 조회", description = "댓글 조회 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 조회 성공"),
            @ApiResponse(responseCode = "401", description = "Authorizeation 오류"),
            @ApiResponse(responseCode = "403", description = "권한 오류"),
            @ApiResponse(responseCode = "404", description = "보드가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("")
    public ResponseEntity<Api_Response<Object>> findComment(@AuthenticationPrincipal Long token, @PathVariable UUID boardId) {
        List<CommentDto> commentDtoList = commentService.getCommentList(token, boardId);
        return ApiResponseUtil.createResponse(
                SuccessCode.SELECT_SUCCESS.getStatus(),
                SuccessCode.SELECT_SUCCESS.getMessage(),
                commentDtoList
        );
    }

    @Operation(summary = "comment 추가", description = "댓글 추가 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 request"),
            @ApiResponse(responseCode = "401", description = "Authorizeation 오류"),
            @ApiResponse(responseCode = "403", description = "권한 오류"),
            @ApiResponse(responseCode = "404", description = "보드가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("")
    public ResponseEntity<Api_Response<Object>> addComment(@AuthenticationPrincipal Long token, @PathVariable UUID boardId, @RequestBody InputCommentDto input) {
        UUID commentId = commentService.addComment(token, boardId, input);
        Map<String, UUID> map = new HashMap<>();
        map.put("CommentId", commentId);

        return ApiResponseUtil.createResponse(
                SuccessCode.INSERT_SUCCESS.getStatus(),
                SuccessCode.INSERT_SUCCESS.getMessage(),
                map
        );
    }

    @Operation(summary = "comment 수정", description = "댓글 수정 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 request"),
            @ApiResponse(responseCode = "401", description = "Authorizeation 오류"),
            @ApiResponse(responseCode = "403", description = "권한 오류"),
            @ApiResponse(responseCode = "404", description = "댓글이 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping("/{commentId}")
    public ResponseEntity<Api_Response<Object>> fixComment(@AuthenticationPrincipal Long token, @PathVariable UUID commentId, @RequestBody InputCommentDto input) {
        commentService.fixComment(token, commentId, input);
        return ApiResponseUtil.createSuccessResponse(
                SuccessCode.UPDATE_SUCCESS.getMessage()
        );
    }

    @Operation(summary = "comment 삭제", description = "댓글 삭제 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "Authorizeation 오류"),
            @ApiResponse(responseCode = "403", description = "권한 오류"),
            @ApiResponse(responseCode = "404", description = "댓글이 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Api_Response<Object>> deleteComment(@AuthenticationPrincipal Long token, @PathVariable UUID commentId) {
        commentService.deleteComment(token, commentId);
        return ApiResponseUtil.createSuccessResponse(
                SuccessCode.DELETE_SUCCESS.getMessage()
        );
    }
}
