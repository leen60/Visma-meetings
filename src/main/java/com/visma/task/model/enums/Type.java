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
// Type (Fixed values - Live / InPerson)
public enum Type {
    @JsonProperty("Live")
    LIVE,
    @JsonProperty("InPerson")
    INPERSON
}