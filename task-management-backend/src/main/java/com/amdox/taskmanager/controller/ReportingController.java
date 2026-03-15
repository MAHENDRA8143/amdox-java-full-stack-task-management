package com.amdox.taskmanager.controller;

import com.amdox.taskmanager.service.ReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/reports")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class ReportingController {

    @Autowired
    private ReportingService reportingService;

    @GetMapping("/burndown")
    public ResponseEntity<Map<String, Object>> burndown(
        @RequestParam(required = false) Long assignedToId,
        @RequestParam(required = false) Long createdById,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String priority,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDueDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDueDate
    ) {
        return ResponseEntity.ok(reportingService.burndown(
            assignedToId,
            createdById,
            status,
            priority,
            fromDueDate,
            toDueDate
        ));
    }

    @GetMapping("/velocity")
    public ResponseEntity<Map<String, Object>> velocity(
        @RequestParam(required = false) Long assignedToId,
        @RequestParam(required = false) Long createdById,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String priority,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDueDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDueDate
    ) {
        return ResponseEntity.ok(reportingService.velocity(
            assignedToId,
            createdById,
            status,
            priority,
            fromDueDate,
            toDueDate
        ));
    }

    @GetMapping("/task-summary")
    public ResponseEntity<Map<String, Object>> taskSummary(
        @RequestParam(required = false) Long assignedToId,
        @RequestParam(required = false) Long createdById,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String priority,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDueDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDueDate
    ) {
        return ResponseEntity.ok(reportingService.taskReport(
            assignedToId,
            createdById,
            status,
            priority,
            fromDueDate,
            toDueDate
        ));
    }

    @GetMapping("/workload")
    public ResponseEntity<Map<String, Object>> workload(
        @RequestParam(required = false) Long assignedToId,
        @RequestParam(required = false) Long createdById,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String priority,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDueDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDueDate
    ) {
        return ResponseEntity.ok(reportingService.workload(
            assignedToId,
            createdById,
            status,
            priority,
            fromDueDate,
            toDueDate
        ));
    }

    @GetMapping("/cumulative-flow")
    public ResponseEntity<Map<String, Object>> cumulativeFlow(
        @RequestParam(required = false) Long assignedToId,
        @RequestParam(required = false) Long createdById,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String priority,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDueDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDueDate
    ) {
        return ResponseEntity.ok(reportingService.cumulativeFlow(
            assignedToId,
            createdById,
            status,
            priority,
            fromDueDate,
            toDueDate
        ));
    }

    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> overview(
        @RequestParam(required = false) Long assignedToId,
        @RequestParam(required = false) Long createdById,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String priority,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDueDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDueDate
    ) {
        return ResponseEntity.ok(reportingService.overview(
            assignedToId,
            createdById,
            status,
            priority,
            fromDueDate,
            toDueDate
        ));
    }
}