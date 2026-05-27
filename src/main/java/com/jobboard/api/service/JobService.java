package com.jobboard.api.service;

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

    public Page<Job> getAllJobs(Pageable pageable) {
        return jobRepository.findAll(pageable);
    }

    public Optional<Job> getJobById(Long id) {
        return jobRepository.findById(id);
    }

    public Job createJob(Job job, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        job.setPostedBy(user);
        return jobRepository.save(job);
    }

    public Optional<Job> updateJob(Long id, Job updatedJob, String username) {
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
            return jobRepository.save(existing);
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

    public Page<Job> searchJobs(String title, String location, Job.JobType jobType, Pageable pageable) {
        return jobRepository.searchJobs(title, location, jobType, pageable);
    }

    // kept for unit tests
    public List<Job> searchByTitle(String title) {
        return jobRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<Job> searchByLocation(String location) {
        return jobRepository.findByLocationContainingIgnoreCase(location);
    }
}