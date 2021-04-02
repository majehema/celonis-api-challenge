package com.celonis.challenge.controllers;

import com.celonis.challenge.services.JobService;
import org.jobrunr.jobs.Job;
import org.jobrunr.jobs.context.JobContext;
import org.jobrunr.jobs.states.StateName;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.server.BackgroundJobServer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    @Autowired
    private final JobService jobService;

    @Autowired
    private final JobScheduler jobScheduler;

    @Autowired
    private final BackgroundJobServer backgroundJobServer;

    public JobController(JobService jobService, JobScheduler jobScheduler, BackgroundJobServer backgroundJobServer) {
        this.jobService = jobService;
        this.jobScheduler = jobScheduler;
        this.backgroundJobServer = backgroundJobServer;
    }

    @GetMapping("/count")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void runJob(@RequestParam(value = "x", required = false) Integer x,
                       @RequestParam(value = "y", required = false) Integer y) {
        if (x != null && y != null) {
            jobScheduler.enqueue(() -> jobService.counter(x, y, JobContext.Null)).toString();
        }
    }

    @GetMapping(path = "/", produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Object> listJobs() throws JSONException {
        String message;

        JSONArray array = new JSONArray();

        // Add jobs to response
        for (StateName state : StateName.values()) {
            for (Job job : jobService.listJobs(state)) {
                JSONObject item = new JSONObject();

                item.put("id", job.getId().toString());
                item.put("jobName", job.getJobName());
                item.put("state", job.getState());
                item.put("createdAt", job.getCreatedAt());
                item.put("updatedAt", job.getUpdatedAt());
                array.put(item);
            }
        }

        message = array.toString();

        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @GetMapping(path = "/{jobId}", produces= MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Object> getJob(@PathVariable UUID jobId) throws JSONException {
        String message;
        Job job = jobService.job(jobId);
        JSONObject item = new JSONObject();

        item.put("id", job.getId().toString());
        item.put("jobName", job.getJobName());
        item.put("state", job.getState());
        item.put("createdAt", job.getCreatedAt());
        item.put("updatedAt", job.getUpdatedAt());

        message = item.toString();

        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @GetMapping("/{jobId}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelJob(@PathVariable UUID jobId){
        jobScheduler.delete(jobId);
    }


}
