package com.amdox.taskmanager.service;

import com.amdox.taskmanager.dto.CommentRequest;
import com.amdox.taskmanager.model.Comment;
import com.amdox.taskmanager.model.Task;
import com.amdox.taskmanager.model.User;
import com.amdox.taskmanager.repository.CommentRepository;
import com.amdox.taskmanager.repository.TaskRepository;
import com.amdox.taskmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    public Comment addComment(Long taskId, CommentRequest request, Long userId) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        Optional<User> userOpt = userRepository.findById(userId);

        if (taskOpt.isEmpty() || userOpt.isEmpty()) {
            return null;
        }

        Comment comment = new Comment();
        comment.setMessage(request.getMessage());
        comment.setTask(taskOpt.get());
        comment.setUser(userOpt.get());

        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsByTask(Long taskId) {
        return commentRepository.findByTaskId(taskId);
    }

    public Optional<Comment> getCommentById(Long id) {
        return commentRepository.findById(id);
    }

    public boolean deleteComment(Long id, Long userId) {
        Optional<Comment> commentOpt = commentRepository.findById(id);

        if (commentOpt.isEmpty()) {
            return false;
        }

        Comment comment = commentOpt.get();

        User user = userRepository.findById(userId).orElse(null);
        if (user != null && !user.getRole().equals("ADMIN")) {
            if (!comment.getUser().getId().equals(userId)) {
                return false;
            }
        }

        commentRepository.deleteById(id);
        return true;
    }
}
