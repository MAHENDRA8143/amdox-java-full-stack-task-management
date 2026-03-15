package com.amdox.taskmanager.service;

import com.amdox.taskmanager.model.Task;
import com.amdox.taskmanager.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportingService {

    @Autowired
    private TaskRepository taskRepository;

    public Map<String, Object> burndown(
        Long assignedToId,
        Long createdById,
        String status,
        String priority,
        LocalDate fromDueDate,
        LocalDate toDueDate
    ) {
        List<Task> tasks = filterTasks(assignedToId, createdById, status, priority, fromDueDate, toDueDate);

        Map<String, Object> response = new HashMap<>();
        Map<String, Object> chart = new LinkedHashMap<>();

        if (tasks.isEmpty()) {
            response.put("totalTasks", 0);
            response.put("burnDown", chart);
            return response;
        }

        LocalDate start = tasks.stream()
            .map(Task::getCreatedAt)
            .filter(v -> v != null)
            .map(LocalDateTime::toLocalDate)
            .min(LocalDate::compareTo)
            .orElse(LocalDate.now());

        LocalDate end = tasks.stream()
            .map(Task::getDueDate)
            .filter(v -> v != null)
            .max(LocalDate::compareTo)
            .orElse(LocalDate.now());

        if (end.isBefore(start)) {
            end = start;
        }

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            LocalDate current = date;
            long remaining = tasks.stream()
                .filter(t -> !"DONE".equalsIgnoreCase(t.getStatus()))
                .filter(t -> t.getDueDate() == null || !t.getDueDate().isBefore(current))
                .count();

            chart.put(current.toString(), remaining);
        }

        response.put("totalTasks", tasks.size());
        response.put("burnDown", chart);
        return response;
    }

    public Map<String, Object> velocity(
        Long assignedToId,
        Long createdById,
        String status,
        String priority,
        LocalDate fromDueDate,
        LocalDate toDueDate
    ) {
        List<Task> completedTasks = filterTasks(assignedToId, createdById, status, priority, fromDueDate, toDueDate).stream()
            .filter(t -> "DONE".equalsIgnoreCase(t.getStatus()))
            .collect(Collectors.toList());

        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        Map<String, Integer> velocity = new LinkedHashMap<>();

        for (Task task : completedTasks) {
            LocalDateTime baseTime = task.getUpdatedAt() != null ? task.getUpdatedAt() : task.getCreatedAt();
            if (baseTime == null) {
                continue;
            }

            int week = baseTime.get(weekFields.weekOfWeekBasedYear());
            int year = baseTime.getYear();
            String bucket = year + "-W" + week;
            velocity.put(bucket, velocity.getOrDefault(bucket, 0) + 1);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("completedTasks", completedTasks.size());
        response.put("velocity", velocity);
        return response;
    }

    public Map<String, Object> taskReport(
        Long assignedToId,
        Long createdById,
        String status,
        String priority,
        LocalDate fromDueDate,
        LocalDate toDueDate
    ) {
        List<Task> tasks = filterTasks(assignedToId, createdById, status, priority, fromDueDate, toDueDate);

        long completed = tasks.stream()
            .filter(t -> "DONE".equalsIgnoreCase(t.getStatus()))
            .count();

        long inProgress = tasks.stream()
            .filter(t -> "IN_PROGRESS".equalsIgnoreCase(t.getStatus()))
            .count();

        long todo = tasks.size() - completed - inProgress;

        Map<String, Object> response = new HashMap<>();
        response.put("totalTasks", tasks.size());
        response.put("completed", completed);
        response.put("inProgress", inProgress);
        response.put("todo", todo);
        response.put("incomplete", tasks.size() - completed);

        Map<String, Long> byPriority = tasks.stream()
            .collect(Collectors.groupingBy(
                t -> t.getPriority() != null ? t.getPriority().toUpperCase() : "UNKNOWN",
                Collectors.counting()
            ));

        response.put("byPriority", byPriority);
        return response;
    }

    public Map<String, Object> workload(
        Long assignedToId,
        Long createdById,
        String status,
        String priority,
        LocalDate fromDueDate,
        LocalDate toDueDate
    ) {
        List<Task> tasks = filterTasks(assignedToId, createdById, status, priority, fromDueDate, toDueDate);

        Map<String, Long> workload = tasks.stream()
            .collect(Collectors.groupingBy(
                t -> t.getAssignedTo() != null ? t.getAssignedTo().getEmail() : "UNASSIGNED",
                Collectors.counting()
            ));

        Map<String, Object> response = new HashMap<>();
        response.put("workload", workload);
        return response;
    }

    public Map<String, Object> cumulativeFlow(
        Long assignedToId,
        Long createdById,
        String status,
        String priority,
        LocalDate fromDueDate,
        LocalDate toDueDate
    ) {
        List<Task> tasks = filterTasks(assignedToId, createdById, status, priority, fromDueDate, toDueDate);

        Map<String, Long> cumulativeFlow = tasks.stream()
            .collect(Collectors.groupingBy(
                t -> t.getStatus() != null ? t.getStatus().toUpperCase() : "UNKNOWN",
                Collectors.counting()
            ));

        Map<String, Object> response = new HashMap<>();
        response.put("cumulativeFlow", cumulativeFlow);
        return response;
    }

    public Map<String, Object> overview(
        Long assignedToId,
        Long createdById,
        String status,
        String priority,
        LocalDate fromDueDate,
        LocalDate toDueDate
    ) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("filters", buildFilterMap(assignedToId, createdById, status, priority, fromDueDate, toDueDate));
        response.put("summary", taskReport(assignedToId, createdById, status, priority, fromDueDate, toDueDate));
        response.put("burndown", burndown(assignedToId, createdById, status, priority, fromDueDate, toDueDate));
        response.put("velocity", velocity(assignedToId, createdById, status, priority, fromDueDate, toDueDate));
        response.put("workload", workload(assignedToId, createdById, status, priority, fromDueDate, toDueDate));
        response.put("cumulativeFlow", cumulativeFlow(assignedToId, createdById, status, priority, fromDueDate, toDueDate));
        return response;
    }

    private List<Task> filterTasks(
        Long assignedToId,
        Long createdById,
        String status,
        String priority,
        LocalDate fromDueDate,
        LocalDate toDueDate
    ) {
        List<Task> tasks = new ArrayList<>(taskRepository.findAll());

        if (assignedToId != null) {
            tasks = tasks.stream()
                .filter(t -> t.getAssignedTo() != null && assignedToId.equals(t.getAssignedTo().getId()))
                .collect(Collectors.toList());
        }

        if (createdById != null) {
            tasks = tasks.stream()
                .filter(t -> t.getCreatedBy() != null && createdById.equals(t.getCreatedBy().getId()))
                .collect(Collectors.toList());
        }

        if (status != null && !status.isBlank()) {
            tasks = tasks.stream()
                .filter(t -> t.getStatus() != null && status.equalsIgnoreCase(t.getStatus()))
                .collect(Collectors.toList());
        }

        if (priority != null && !priority.isBlank()) {
            tasks = tasks.stream()
                .filter(t -> t.getPriority() != null && priority.equalsIgnoreCase(t.getPriority()))
                .collect(Collectors.toList());
        }

        if (fromDueDate != null) {
            tasks = tasks.stream()
                .filter(t -> t.getDueDate() != null && !t.getDueDate().isBefore(fromDueDate))
                .collect(Collectors.toList());
        }

        if (toDueDate != null) {
            tasks = tasks.stream()
                .filter(t -> t.getDueDate() != null && !t.getDueDate().isAfter(toDueDate))
                .collect(Collectors.toList());
        }

        return tasks;
    }

    private Map<String, Object> buildFilterMap(
        Long assignedToId,
        Long createdById,
        String status,
        String priority,
        LocalDate fromDueDate,
        LocalDate toDueDate
    ) {
        Map<String, Object> filters = new LinkedHashMap<>();
        filters.put("assignedToId", assignedToId);
        filters.put("createdById", createdById);
        filters.put("status", status);
        filters.put("priority", priority);
        filters.put("fromDueDate", fromDueDate != null ? fromDueDate.toString() : null);
        filters.put("toDueDate", toDueDate != null ? toDueDate.toString() : null);
        return filters;
    }
}