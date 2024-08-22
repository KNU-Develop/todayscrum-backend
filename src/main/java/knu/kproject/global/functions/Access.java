package knu.kproject.global.functions;

import knu.kproject.entity.board.Board;
import knu.kproject.entity.comment.Comment;
import knu.kproject.entity.project.Project;
import knu.kproject.entity.project.ProjectUser;
import knu.kproject.entity.user.User;
import knu.kproject.exception.ProjectException;
import knu.kproject.exception.code.ProjectErrorCode;
import knu.kproject.global.CHOICE;
import knu.kproject.global.ROLE;

public class Access {
    // 기본 인증
    public static void accessPossible(ProjectUser projectUser, ROLE require) {
        if (projectUser == null) {
            throw new ProjectException(ProjectErrorCode.FORBIDEN_ROLE);
        }
        if (projectUser.getRole().equals(ROLE.WRITER) && require.equals(ROLE.OWNER)) {
            throw new ProjectException(ProjectErrorCode.FORBIDEN_ROLE);
        } else if (projectUser.getRole().equals(ROLE.GUEST) && (require.equals(ROLE.WRITER) || require.equals(ROLE.OWNER))) {
            throw new ProjectException(ProjectErrorCode.FORBIDEN_ROLE);
        } else if (projectUser.getChoice() != null && (projectUser.getChoice().equals(CHOICE.전송) || projectUser.getChoice().equals(CHOICE.거절)) && (require.equals(ROLE.WRITER) || require.equals(ROLE.OWNER))) {
            throw new ProjectException(ProjectErrorCode.INVITED_YET);
        } else if (projectUser.getChoice() == null && !projectUser.getRole().equals(ROLE.MASTER)) {
            throw new ProjectException(ProjectErrorCode.NOT_INVITE);
        }
    }

    // 보드 수정, 삭제 시 인증
    public static void accessBoard(ProjectUser projectUser, Board board) {
        if (!board.getUser().equals(projectUser.getUser()) && projectUser.getRole().equals(ROLE.WRITER)) {
            throw new ProjectException(ProjectErrorCode.FORBIDEN_ROLE);
        } else if (projectUser.getChoice() != null && ((projectUser.getChoice().equals(CHOICE.전송) || projectUser.getChoice().equals(CHOICE.거절)) || projectUser.getChoice().equals(CHOICE.전송))) {
            throw new ProjectException(ProjectErrorCode.INVITED_YET);
        } else if (projectUser.getChoice() == null && !projectUser.getRole().equals(ROLE.MASTER)) {
            throw new ProjectException(ProjectErrorCode.NOT_INVITE);
        }
    }

    public static boolean accessBoard2(ProjectUser projectUser, Board board) {
        if (!board.getUser().equals(projectUser.getUser()) && projectUser.getRole().equals(ROLE.WRITER)) {
            return false;
        } else if (projectUser.getChoice() != null && ((projectUser.getChoice().equals(CHOICE.전송) || projectUser.getChoice().equals(CHOICE.거절)) || projectUser.getChoice().equals(CHOICE.전송))) {
            return false;
        } else if (projectUser.getChoice() == null && !projectUser.getRole().equals(ROLE.MASTER)) {
            return false;
        }
        return true;
    }

    // 댓글 수정, 삭제 시 인증
    public static boolean accessComment(ProjectUser projectUser, Comment comment) {
        if (projectUser.getRole().equals(ROLE.OWNER) || projectUser.getRole().equals(ROLE.MASTER)) {
            return true;
        } else if (projectUser.getRole().equals(ROLE.WRITER)) {
            if (comment.getUser().equals(projectUser.getUser())) {
                return true;
            } else if (comment.getBoard().getUser().equals(projectUser.getUser())) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static void accessComment2(ProjectUser projectUser, Comment comment) {
        if (projectUser.getRole().equals(ROLE.OWNER) || projectUser.getRole().equals(ROLE.MASTER)) {
            return;
        } else if (projectUser.getRole().equals(ROLE.WRITER)) {
            if (comment.getUser().equals(projectUser.getUser())) {
                return;
            } else if (comment.getBoard().getUser().equals(projectUser.getUser())) {
                return;
            } else {
                throw new ProjectException(ProjectErrorCode.INVITED_YET);
            }
        } else {
            throw new ProjectException(ProjectErrorCode.NOT_INVITE);
        }
    }
}
