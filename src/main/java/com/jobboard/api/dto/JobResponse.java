package com.jobboard.api.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class JobResponse {
    private Long id;
    private String title;
    private String company;
    private String location;
    private String jobType;
    private double salary;
    private String description;
    private LocalDateTime createdAt;
    private String postedBy;
}