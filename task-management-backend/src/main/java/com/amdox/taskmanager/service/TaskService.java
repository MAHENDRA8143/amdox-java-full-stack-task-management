package com.amdox.taskmanager.service;

import com.amdox.taskmanager.dto.TaskRequest;
import com.amdox.taskmanager.model.Task;
import com.amdox.taskmanager.model.User;
import com.amdox.taskmanager.repository.TaskRepository;
import com.amdox.taskmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    public Task createTask(TaskRequest request, Long createdById) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority() != null ? request.getPriority() : "MEDIUM");
        task.setStatus(request.getStatus() != null ? request.getStatus() : "TODO");
        task.setDueDate(request.getDueDate());

        User createdBy = userRepository.findById(createdById).orElse(null);
        task.setCreatedBy(createdBy);

        if (request.getAssignedToId() != null) {
            User assignedTo = userRepository.findById(request.getAssignedToId()).orElse(null);
            task.setAssignedTo(assignedTo);
        }

        return taskRepository.save(task);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public Task updateTask(Long id, TaskRequest request, Long userId) {
        Optional<Task> taskOpt = taskRepository.findById(id);

        if (taskOpt.isEmpty()) {
            return null;
        }

        Task task = taskOpt.get();

        User user = userRepository.findById(userId).orElse(null);
        if (user != null && !user.getRole().equals("ADMIN")) {
            if (!task.getCreatedBy().getId().equals(userId)) {
                return null;
            }
        }

        task.setTitle(request.getTitle() != null ? request.getTitle() : task.getTitle());
        task.setDescription(request.getDescription() != null ? request.getDescription() : task.getDescription());
        task.setPriority(request.getPriority() != null ? request.getPriority() : task.getPriority());
        task.setStatus(request.getStatus() != null ? request.getStatus() : task.getStatus());
        task.setDueDate(request.getDueDate() != null ? request.getDueDate() : task.getDueDate());

        if (request.getAssignedToId() != null) {
            User assignedTo = userRepository.findById(request.getAssignedToId()).orElse(null);
            task.setAssignedTo(assignedTo);
        }

        return taskRepository.save(task);
    }

    public boolean deleteTask(Long id, Long userId) {
        Optional<Task> taskOpt = taskRepository.findById(id);

        if (taskOpt.isEmpty()) {
            return false;
        }

        Task task = taskOpt.get();

        User user = userRepository.findById(userId).orElse(null);
        if (user != null && !user.getRole().equals("ADMIN")) {
            if (!task.getCreatedBy().getId().equals(userId)) {
                return false;
            }
        }

        taskRepository.deleteById(id);
        return true;
    }

    public List<Task> getTasksByStatus(String status) {
        return taskRepository.findByStatus(status);
    }

    public List<Task> getTasksByAssignedUser(Long userId) {
        return taskRepository.findByAssignedToId(userId);
    }

    public List<Task> getOverdueTasks() {
        return taskRepository.findOverdueTasks(LocalDate.now());
    }

    public List<Task> getTasksByPriority(String priority) {
        return taskRepository.findByPriority(priority);
    }
}
