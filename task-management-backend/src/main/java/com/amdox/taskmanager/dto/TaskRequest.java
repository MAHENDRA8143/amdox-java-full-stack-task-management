package com.amdox.taskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {
    private String title;
    private String description;
    private String priority; // HIGH, MEDIUM, LOW
    private String status; // TODO, IN_PROGRESS, DONE
    private LocalDate dueDate;
    private Long assignedToId;
}
