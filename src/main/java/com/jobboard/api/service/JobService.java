package com.jobboard.api.service;

import com.jobboard.api.dto.JobResponse;
import com.jobboard.api.model.Job;
import com.jobboard.api.model.User;
import com.jobboard.api.repository.JobRepository;
import com.jobboard.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    private JobResponse toResponse(Job job) {
        JobResponse r = new JobResponse();
        r.setId(job.getId());
        r.setTitle(job.getTitle());
        r.setCompany(job.getCompany());
        r.setLocation(job.getLocation());
        r.setJobType(job.getJobType().name());
        r.setSalary(job.getSalary());
        r.setDescription(job.getDescription());
        r.setCreatedAt(job.getCreatedAt());
        r.setPostedBy(job.getPostedBy().getUsername());
        return r;
    }

    public Page<JobResponse> getAllJobs(Pageable pageable) {
        return jobRepository.findAll(pageable).map(this::toResponse);
    }

    public Optional<JobResponse> getJobById(Long id) {
        return jobRepository.findById(id).map(this::toResponse);
    }

    public JobResponse createJob(Job job, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        job.setPostedBy(user);
        return toResponse(jobRepository.save(job));
    }

    public Optional<JobResponse> updateJob(Long id, Job updatedJob, String username) {
        return jobRepository.findById(id).map(existing -> {
            if (!existing.getPostedBy().getUsername().equals(username)) {
                throw new RuntimeException("Not authorized to update this job");
            }
            existing.setTitle(updatedJob.getTitle());
            existing.setCompany(updatedJob.getCompany());
            existing.setDescription(updatedJob.getDescription());
            existing.setLocation(updatedJob.getLocation());
            existing.setSalary(updatedJob.getSalary());
            existing.setJobType(updatedJob.getJobType());
            return toResponse(jobRepository.save(existing));
        });
    }

    public boolean deleteJob(Long id, String username) {
        return jobRepository.findById(id).map(job -> {
            if (!job.getPostedBy().getUsername().equals(username)) {
                throw new RuntimeException("Not authorized to delete this job");
            }
            jobRepository.delete(job);
            return true;
        }).orElse(false);
    }

    public Page<JobResponse> searchJobs(String title, String location, Job.JobType jobType, Pageable pageable) {
        return jobRepository.searchJobs(title, location, jobType, pageable).map(this::toResponse);
    }

    public List<Job> searchByTitle(String title) {
        return jobRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<Job> searchByLocation(String location) {
        return jobRepository.findByLocationContainingIgnoreCase(location);
    }
}