package com.celonis.challenge.controllers;

import com.celonis.challenge.model.ProjectGenerationTask;
import com.celonis.challenge.services.JobService;
import org.jobrunr.jobs.Job;
import org.jobrunr.jobs.context.JobContext;
import org.jobrunr.jobs.states.StateName;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.server.BackgroundJobServer;
import org.jobrunr.storage.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    @Autowired
    private final JobService jobService;

    @Autowired
    private JobScheduler jobScheduler;

    public JobController(JobService jobService, JobScheduler jobScheduler) {
        this.jobService = jobService;
        this.jobScheduler = jobScheduler;
    }


    @GetMapping("/count")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void runJob(@RequestParam(value = "x", required = false) Integer x,
                       @RequestParam(value = "y", required = false) Integer y) {
        if (x != null && y != null) {
            String uuid = jobScheduler.enqueue(() -> jobService.counter(x, y, JobContext.Null)).toString();
            System.out.println("jobId:" + uuid);
        }
    }

    @GetMapping("/")
    public List<Job> listJobs() {
        return jobService.listJobs();
    }

    @GetMapping("/{jobId}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelJob(@PathVariable UUID jobId){
        jobScheduler.delete(jobId);
    }


}
