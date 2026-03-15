package com.amdox.taskmanager.service;

import com.amdox.taskmanager.model.Comment;
import com.amdox.taskmanager.model.Task;
import com.amdox.taskmanager.model.User;
import com.amdox.taskmanager.repository.CommentRepository;
import com.amdox.taskmanager.repository.TaskRepository;
import com.amdox.taskmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class IntegrationService {

    private static final Pattern ISSUE_KEY_PATTERN = Pattern.compile("([A-Z]+)-(\\d+)");

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    public void handleCommitMessage(String commitMsg, String author) {
        Long taskId = extractTaskId(commitMsg);
        if (taskId == null) {
            return;
        }

        updateTaskStatusAndComment(taskId, "DONE", author, "Automatically closed by commit: " + commitMsg);
    }

    public void handlePullRequest(String title, String author) {
        Long taskId = extractTaskId(title);
        if (taskId == null) {
            return;
        }

        updateTaskStatusAndComment(taskId, "IN_PROGRESS", author, "Pull request opened: " + title);
    }

    public void processGithubEvent(String event, Map<String, Object> payload) {
        if ("PUSH".equalsIgnoreCase(event)) {
            Object commitObj = payload.get("commits");
            if (!(commitObj instanceof List<?> commits)) {
                return;
            }

            for (Object obj : commits) {
                if (!(obj instanceof Map<?, ?> rawCommit)) {
                    continue;
                }

                @SuppressWarnings("unchecked")
                Map<String, Object> commit = (Map<String, Object>) rawCommit;
                String message = stringValue(commit.get("message"));

                String author = "Unknown";
                Object authorObj = commit.get("author");
                if (authorObj instanceof Map<?, ?> rawAuthor) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> authorMap = (Map<String, Object>) rawAuthor;
                    author = stringValue(authorMap.get("name"));
                }

                handleCommitMessage(message, author);
            }
            return;
        }

        if ("PULL_REQUEST".equalsIgnoreCase(event)) {
            Object prObj = payload.get("pull_request");
            if (!(prObj instanceof Map<?, ?> rawPr)) {
                return;
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> pr = (Map<String, Object>) rawPr;

            String title = stringValue(pr.get("title"));
            String author = "Unknown";

            Object userObj = pr.get("user");
            if (userObj instanceof Map<?, ?> rawUser) {
                @SuppressWarnings("unchecked")
                Map<String, Object> userMap = (Map<String, Object>) rawUser;
                author = stringValue(userMap.get("login"));
            }

            handlePullRequest(title, author);
        }
    }

    public void processJenkinsEvent(Map<String, Object> body) {
        String jobName = stringValue(body.get("name"));
        String result = stringValue(body.get("result"));
        String log = stringValue(body.get("log"));

        Long taskId = extractTaskId(jobName);
        if (taskId == null) {
            return;
        }

        if ("FAILURE".equalsIgnoreCase(result)) {
            updateTaskStatusAndComment(taskId, "TODO", "Jenkins", "Build failed. Log: " + log);
        }
    }

    public void processDockerEvent(Map<String, Object> payload) {
        String status = stringValue(payload.get("status"));
        String image = stringValue(payload.get("from"));

        String containerName = "";
        String imageName = image;

        Object actorObj = payload.get("Actor");
        if (actorObj instanceof Map<?, ?> rawActor) {
            @SuppressWarnings("unchecked")
            Map<String, Object> actor = (Map<String, Object>) rawActor;
            Object attributesObj = actor.get("Attributes");

            if (attributesObj instanceof Map<?, ?> rawAttributes) {
                @SuppressWarnings("unchecked")
                Map<String, Object> attributes = (Map<String, Object>) rawAttributes;
                containerName = stringValue(attributes.get("name"));
                if (attributes.get("image") != null) {
                    imageName = stringValue(attributes.get("image"));
                }
            }
        }

        Long taskId = extractTaskId(containerName + " " + imageName);
        if (taskId == null) {
            return;
        }

        if ("start".equalsIgnoreCase(status)) {
            updateTaskStatusAndComment(taskId, "IN_PROGRESS", "Docker", "Container started. Image: " + imageName);
        } else if ("die".equalsIgnoreCase(status)) {
            updateTaskStatusAndComment(taskId, "TODO", "Docker", "Container crashed. Image: " + imageName);
        } else if ("pull".equalsIgnoreCase(status)) {
            appendComment(taskId, "Docker", "Image pulled: " + imageName);
        } else if ("build".equalsIgnoreCase(status)) {
            appendComment(taskId, "Docker", "Image built: " + imageName);
        } else {
            appendComment(taskId, "Docker", "Docker event: " + status + " | Image: " + imageName);
        }
    }

    private Long extractTaskId(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }

        Matcher matcher = ISSUE_KEY_PATTERN.matcher(text);
        if (matcher.find()) {
            return Long.parseLong(matcher.group(2));
        }
        return null;
    }

    private void updateTaskStatusAndComment(Long taskId, String status, String author, String commentBody) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            return;
        }

        Task task = taskOpt.get();
        task.setStatus(status);
        taskRepository.save(task);

        appendComment(taskId, author, commentBody);
    }

    private void appendComment(Long taskId, String author, String message) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            return;
        }

        Task task = taskOpt.get();
        User commentUser = resolveCommentUser(author, task);
        if (commentUser == null) {
            return;
        }

        Comment comment = new Comment();
        comment.setTask(task);
        comment.setUser(commentUser);
        comment.setMessage(message);
        commentRepository.save(comment);
    }

    private User resolveCommentUser(String author, Task task) {
        if (author != null && !author.isBlank()) {
            Optional<User> byEmail = userRepository.findByEmail(author);
            if (byEmail.isPresent()) {
                return byEmail.get();
            }
        }

        if (task.getCreatedBy() != null) {
            return task.getCreatedBy();
        }

        return task.getAssignedTo();
    }

    private String stringValue(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}