package knu.kproject.global.code;

import lombok.Getter;

@Getter
public enum ScheduleType {

    DELETE_ALL("ALL"),
    DELETE_BEFORE("BEFORE"),
    DELETE_THIS("THIS")
    ;

    private final String type;

    ScheduleType(String type) {
        this.type = type;
    }
}
