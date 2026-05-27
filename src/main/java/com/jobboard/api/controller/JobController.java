package com.jobboard.api.controller;

import com.jobboard.api.model.Job;
import com.jobboard.api.service.JobService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    @Autowired
    private JobService jobService;

    @GetMapping
    public ResponseEntity<List<Job>> getAllJobs() {
        return ResponseEntity.ok(jobService.getAllJobs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getJobById(@PathVariable Long id) {
        return jobService.getJobById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Job>> searchJobs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location) {
        if (title != null) {
            return ResponseEntity.ok(jobService.searchByTitle(title));
        }
        if (location != null) {
            return ResponseEntity.ok(jobService.searchByLocation(location));
        }
        return ResponseEntity.ok(jobService.getAllJobs());
    }

    @PostMapping
    public ResponseEntity<?> createJob(@Valid @RequestBody Job job,
                                       Authentication authentication) {
        Job created = jobService.createJob(job, authentication.getName());
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateJob(@PathVariable Long id,
                                       @Valid @RequestBody Job job,
                                       Authentication authentication) {
        return jobService.updateJob(id, job, authentication.getName())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJob(@PathVariable Long id,
                                       Authentication authentication) {
        boolean deleted = jobService.deleteJob(id, authentication.getName());
        if (deleted) {
            return ResponseEntity.ok(Map.of("message", "Job deleted successfully"));
        }
        return ResponseEntity.notFound().build();
    }
}