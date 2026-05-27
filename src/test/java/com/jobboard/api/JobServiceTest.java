package com.jobboard.api;

import com.jobboard.api.model.Job;
import com.jobboard.api.model.User;
import com.jobboard.api.repository.JobRepository;
import com.jobboard.api.repository.UserRepository;
import com.jobboard.api.service.JobService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private JobService jobService;

    private User testUser;
    private Job testJob;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encoded_password");
        testUser.setRole(User.Role.USER);

        testJob = new Job();
        testJob.setId(1L);
        testJob.setTitle("Software Engineer");
        testJob.setCompany("TechCorp");
        testJob.setDescription("Build amazing products");
        testJob.setLocation("Austin, TX");
        testJob.setSalary(120000.0);
        testJob.setJobType(Job.JobType.FULL_TIME);
        testJob.setPostedBy(testUser);
    }

    @Test
    void getAllJobs_returnsAllJobs() {
        when(jobRepository.findAll()).thenReturn(List.of(testJob));

        List<Job> jobs = jobService.getAllJobs();

        assertEquals(1, jobs.size());
        assertEquals("Software Engineer", jobs.get(0).getTitle());
        verify(jobRepository, times(1)).findAll();
    }

    @Test
    void getJobById_existingId_returnsJob() {
        when(jobRepository.findById(1L)).thenReturn(Optional.of(testJob));

        Optional<Job> result = jobService.getJobById(1L);

        assertTrue(result.isPresent());
        assertEquals("TechCorp", result.get().getCompany());
    }

    @Test
    void getJobById_nonExistingId_returnsEmpty() {
        when(jobRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Job> result = jobService.getJobById(99L);

        assertFalse(result.isPresent());
    }

    @Test
    void createJob_validUser_savesAndReturnsJob() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(jobRepository.save(any(Job.class))).thenReturn(testJob);

        Job newJob = new Job();
        newJob.setTitle("Software Engineer");
        newJob.setCompany("TechCorp");
        newJob.setDescription("Build amazing products");
        newJob.setLocation("Austin, TX");
        newJob.setSalary(120000.0);
        newJob.setJobType(Job.JobType.FULL_TIME);

        Job result = jobService.createJob(newJob, "testuser");

        assertNotNull(result);
        assertEquals("Software Engineer", result.getTitle());
        verify(jobRepository, times(1)).save(any(Job.class));
    }

    @Test
    void deleteJob_authorizedUser_deletesJob() {
        when(jobRepository.findById(1L)).thenReturn(Optional.of(testJob));

        boolean result = jobService.deleteJob(1L, "testuser");

        assertTrue(result);
        verify(jobRepository, times(1)).delete(testJob);
    }

    @Test
    void deleteJob_unauthorizedUser_throwsException() {
        when(jobRepository.findById(1L)).thenReturn(Optional.of(testJob));

        assertThrows(RuntimeException.class, () ->
                jobService.deleteJob(1L, "otheruser"));

        verify(jobRepository, never()).delete(any());
    }

    @Test
    void searchByTitle_returnsMatchingJobs() {
        when(jobRepository.findByTitleContainingIgnoreCase("engineer"))
                .thenReturn(List.of(testJob));

        List<Job> results = jobService.searchByTitle("engineer");

        assertEquals(1, results.size());
        assertEquals("Software Engineer", results.get(0).getTitle());
    }

    @Test
    void updateJob_unauthorizedUser_throwsException() {
        when(jobRepository.findById(1L)).thenReturn(Optional.of(testJob));

        assertThrows(RuntimeException.class, () ->
                jobService.updateJob(1L, testJob, "otheruser"));
    }
}