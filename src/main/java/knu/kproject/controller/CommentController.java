package knu.kproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Comment API", description = "보드 내 댓글 api 입니다.")
@RequestMapping("workspace/project/board/comment")
public class CommentController {
    private final CommentService commentService;

    @Operation(summary = "comment 조회", description = "댓글 조회 API 입니다.")
    @Parameter(name = "key", description = "boardId 값 입니다.")
    @GetMapping("")
    public ResponseEntity<Api_Response<Object>> findComment(@AuthenticationPrincipal Long token, @RequestParam UUID key) {
        List<CommentDto> commentDtoList = commentService.getCommentList(token, key);
        return ApiResponseUtil.createResponse(
                SuccessCode.SELECT_SUCCESS.getStatus(),
                SuccessCode.SELECT_SUCCESS.getMessage(),
                commentDtoList
        );
    }

    @Operation(summary = "comment 추가", description = "댓글 추가 API 입니다.")
    @Parameter(name = "key", description = "boardId 값 입니다.")
    @PostMapping("")
    public ResponseEntity<Api_Response<Object>> addComment(@AuthenticationPrincipal Long token, @RequestParam UUID key, @RequestBody InputCommentDto input) {
        commentService.addComment(token, key, input);
        return ApiResponseUtil.createSuccessResponse(
                SuccessCode.INSERT_SUCCESS.getMessage()
        );
    }

    @Operation(summary = "comment 수정", description = "댓글 수정 API 입니다.")
    @Parameter(name = "key", description = "commentId 값 입니다.")
    @PutMapping("")
    public ResponseEntity<Api_Response<Object>> fixComment(@AuthenticationPrincipal Long token, @RequestParam UUID key, @RequestBody CommentDto commentDto) {
        commentService.fixComment(token, key, commentDto);
        return ApiResponseUtil.createSuccessResponse(
                SuccessCode.SELECT_SUCCESS.getMessage()
        );
    }

    @Operation(summary = "comment 삭제", description = "댓글 삭제 API 입니다.")
    @Parameter(name = "key", description = "commentId 값 입니다.")
    @DeleteMapping("")
    public ResponseEntity<Api_Response<Object>> deleteComment(@AuthenticationPrincipal Long token, @RequestParam UUID key) {
        commentService.deleteComment(token, key);
        return ApiResponseUtil.createSuccessResponse(
                SuccessCode.SELECT_SUCCESS.getMessage()
        );
    }
}
