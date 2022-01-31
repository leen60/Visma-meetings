package com.visma.task.model.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Getter
@ToString
// ○ Category (Fixed values - CodeMonkey / Hub / Short / TeamBuilding)
public enum Category {
    @JsonProperty("CodeMonkey")
    CODEMONKEY,
    @JsonProperty("Hub")
    HUB,
    @JsonProperty("Short")
    SHORT,
    @JsonProperty("TeamBuilding")
    TEAMBUILDING;
}