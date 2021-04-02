package com.celonis.challenge.services;

import com.celonis.challenge.model.ProjectGenerationTask;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.context.JobContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.jobrunr.jobs.context.JobDashboardProgressBar;
import org.jobrunr.jobs.context.JobRunrDashboardLogger;
import org.jobrunr.jobs.states.StateName;
import org.jobrunr.server.BackgroundJobServer;
import org.jobrunr.storage.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class JobService {

    public Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private BackgroundJobServer backgroundJobServer;

    public JobService(BackgroundJobServer backgroundJobServer) {
        this.backgroundJobServer = backgroundJobServer;
    }

    public List<org.jobrunr.jobs.Job> listJobs() {
        List<org.jobrunr.jobs.Job> jobs = backgroundJobServer.getStorageProvider()
                .getJobs(StateName.PROCESSING, PageRequest.ascOnUpdatedAt(5));
        System.out.println("*******" + jobs);
        return jobs;
    }

    @Job(name = "Job with parameters %0 and %1")
    public void counter(int x, int y, JobContext jobContext) {
        try {
            JobDashboardProgressBar progressBar = jobContext.progressBar(y-x);

            logger.info("Starting job ");
            JobRunrDashboardLogger jobLogger = new JobRunrDashboardLogger(LoggerFactory.getLogger(getClass()));
            for (int i = x; i<= y; i++){
                Thread.sleep(1000);
                progressBar.increaseByOne();
                jobLogger.info(String.valueOf(i));
            }
            logger.info("Job with parameters " + x + " and " + y + " finished");
        } catch (InterruptedException e) {
            if (e.getMessage().equals("sleep interrupted")){
                logger.error("The job with parameters " + x + " and " + y + " was canceled", e);
            } else {
                logger.error("Error while executing job with parameters " + x + " and " + y, e);
            }
        }
    }
}
