package com.visma.task.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BaseEntity<T> {
    T data;
}