package com.amdox.taskmanager.controller;

import com.amdox.taskmanager.service.IntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/integrations")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class IntegrationController {

    @Autowired
    private IntegrationService integrationService;

    @PostMapping("/github")
    public ResponseEntity<String> processGithubEvent(
        @RequestHeader(value = "X-GitHub-Event", required = false) String event,
        @RequestBody Map<String, Object> payload
    ) {
        String normalizedEvent = event != null ? event.toUpperCase() : "";
        integrationService.processGithubEvent(normalizedEvent, payload);
        return ResponseEntity.ok("GitHub event processed successfully");
    }

    @PostMapping("/jenkins")
    public ResponseEntity<String> processJenkinsEvent(@RequestBody Map<String, Object> payload) {
        integrationService.processJenkinsEvent(payload);
        return ResponseEntity.ok("Jenkins event processed successfully");
    }

    @PostMapping("/docker")
    public ResponseEntity<String> processDockerEvent(@RequestBody Map<String, Object> payload) {
        integrationService.processDockerEvent(payload);
        return ResponseEntity.ok("Docker event processed successfully");
    }

    @PostMapping("/commit")
    public ResponseEntity<String> handleCommit(@RequestParam String message, @RequestParam String author) {
        integrationService.handleCommitMessage(message, author);
        return ResponseEntity.ok("Commit processed");
    }

    @PostMapping("/pull-request")
    public ResponseEntity<String> handlePullRequest(@RequestParam String title, @RequestParam String author) {
        integrationService.handlePullRequest(title, author);
        return ResponseEntity.ok("Pull request processed");
    }
}