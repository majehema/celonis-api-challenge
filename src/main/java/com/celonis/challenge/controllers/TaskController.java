package com.celonis.challenge.controllers;

import com.celonis.challenge.model.ProjectGenerationTask;
import com.celonis.challenge.model.ProjectGenerationTaskRepository;
import com.celonis.challenge.services.FileService;
import com.celonis.challenge.services.JobService;
import com.celonis.challenge.services.TaskService;
import org.jobrunr.jobs.context.JobContext;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    private final FileService fileService;

    @Autowired
    private final JobService jobService;

    @Autowired
    private final JobScheduler jobScheduler;

    private final ProjectGenerationTaskRepository projectGenerationTaskRepository;


    public TaskController(TaskService taskService,
                          FileService fileService, JobService jobService, JobScheduler jobScheduler, ProjectGenerationTaskRepository projectGenerationTaskRepository) {
        this.taskService = taskService;
        this.fileService = fileService;
        this.jobService = jobService;
        this.jobScheduler = jobScheduler;
        this.projectGenerationTaskRepository = projectGenerationTaskRepository;
    }

    @GetMapping("/")
    public List<ProjectGenerationTask> listTasks() {
        return taskService.listTasks();
    }

    @PostMapping("/")
    public ProjectGenerationTask createTask(@RequestBody @Valid ProjectGenerationTask projectGenerationTask) {
        return taskService.createTask(projectGenerationTask);
    }

    @GetMapping("/{taskId}")
    public ProjectGenerationTask getTask(@PathVariable String taskId) {
        return taskService.getTask(taskId);
    }

    @PutMapping("/{taskId}")
    public ProjectGenerationTask updateTask(@PathVariable String taskId,
                                            @RequestBody @Valid ProjectGenerationTask projectGenerationTask) {
        return taskService.update(taskId, projectGenerationTask);
    }

    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable String taskId) {
        taskService.delete(taskId);
    }

    @PostMapping("/{taskId}/execute")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void executeTask(@PathVariable String taskId,
                            @RequestParam(value = "x", required = false) Integer x,
                            @RequestParam(value = "y", required = false) Integer y) {
        if (x != null && y != null) {
            String uuid = jobScheduler.enqueue(() -> jobService.counter(x, y, JobContext.Null)).toString();

            ProjectGenerationTask projectGenerationTask = taskService.getTask(taskId);
            projectGenerationTask.setJobId(uuid);
            projectGenerationTaskRepository.save(projectGenerationTask);

        } else {
            taskService.executeTask(taskId);
        }
    }

    @GetMapping("/{taskId}/result")
    public ResponseEntity<FileSystemResource> getResult(@PathVariable String taskId) {
        return fileService.getTaskResult(taskId, taskService);
    }

}
