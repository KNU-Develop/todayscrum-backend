package knu.kproject.global.functions;

import knu.kproject.entity.project.ProjectUser;
import knu.kproject.entity.user.User;
import knu.kproject.global.CHOICE;
import knu.kproject.global.ROLE;

public class Access {
    public static void accessPossible(ProjectUser projectUser, ROLE require) {
        if (projectUser == null) {
            throw new NullPointerException();
        }
        if (projectUser.getRole().equals(ROLE.WRITER) && require.equals(ROLE.OWNER)) {
            throw new NullPointerException();
        } else if (projectUser.getRole().equals(ROLE.GUEST) && require.equals(ROLE.WRITER) || require.equals(ROLE.OWNER)) {
            throw new NullPointerException();
        } else if (projectUser.getChoice().equals(CHOICE.미정) && require.equals(ROLE.WRITER) || require.equals(ROLE.OWNER)) {
            throw new NullPointerException();
        }
    }
}
