import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Task } from '../model/task';
import { Observable } from 'rxjs/Observable';

@Injectable()
export class TaskService {

  private tasksUrl: string;

  constructor(private http: HttpClient) {
    this.tasksUrl = 'http://localhost:8090/api/tasks/';
  }

  public findAll(): Observable<Task[]> {
    return this.http.get<Task[]>(this.tasksUrl);
  }

  public save(task: Task) {
    return this.http.post<Task>(this.tasksUrl, task);
  }
}
