package knu.kproject.dto.comment;

import knu.kproject.entity.user.User;
import lombok.Data;

@Data
public class CommentUser {
    private String name;
    private String imageUrl;

    public CommentUser(User user) {
        this.name = user.getName();
        this.imageUrl = user.getImageUrl();
    }
}
