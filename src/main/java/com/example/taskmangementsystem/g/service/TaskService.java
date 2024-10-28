package com.example.taskmangementsystem.g.service;

import com.example.taskmangementsystem.g.entity.Task;
import com.example.taskmangementsystem.g.repository.TaskRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.*;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
@Service
public class TaskService {
    @Autowired
    private TaskRepo taskRepo;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    public boolean isNameValid(String name) {
        if (name != null) {
            String regex = "^[A-Za-z ]{1,255}$";
            return Pattern.matches(regex, name);
        }
        return false;
    }


    public boolean isLogHoursValid(String logHours) {
        if (logHours != null) {
            String[] parts = logHours.split(":");
            if (parts.length == 2) {
                int hours = Integer.parseInt(parts[0]);
                int minutes = Integer.parseInt(parts[1]);
                return hours < 8 && minutes >= 0 && minutes < 60;
            }
        }
        return false;
    }

    public Task insertTask(Task task){
        System.out.println("saving task");
        if (!isNameValid(task.getName())) {
            throw new IllegalArgumentException("Invalid name.");
        }
        if (!isLogHoursValid(task.getLogHours())) {
            throw new IllegalArgumentException("Invalid log hours.");
        }
        return taskRepo.save(task);
    }

    public Iterable<Task>getTask(){
        return taskRepo.findAll();
    }

    public void deleteAllTasks(){
        taskRepo.deleteAll();
    }

    public Void  deleteTask(String id){
        taskRepo.deleteById(id);
        return null;
    }

    public Task getTaskById(String id){
        Optional<Task> taskOptional =taskRepo.findById(id);
        return taskOptional.orElseThrow(()->new RuntimeException("Task not found with id:"+id));
    }

    public Task updateTask(Task task, String id) {
        Optional<Task>existingTaskOptional=taskRepo.findById(id);
        if(existingTaskOptional.isEmpty()){
            throw new RuntimeException("Task not found with id"+id);
        }
        Task existingTask = existingTaskOptional.get();
        if (!isNameValid(task.getName())) {
            throw new IllegalArgumentException("Invalid name.");
        }
        if (!isLogHoursValid(task.getLogHours())) {
            throw new IllegalArgumentException("Invalid log hours.");
        }


        existingTask.setName(task.getName());
        existingTask.setLogHours(task.getLogHours());
        existingTask.setTask(task.getTask());
        existingTask.setPriority(task.getPriority());
        existingTask.setStatus(task.getStatus());
        existingTask.setDate(task.getDate());
        existingTask.setTime(task.getTime());
        existingTask.setDay(task.getDay());

        return taskRepo.save(existingTask);
    }


    public List<Task> searchByQuery(String query) {
        Criteria criteria = Criteria.where("name").matches(query);
        CriteriaQuery criteriaQuery = new CriteriaQuery(criteria);

        return elasticsearchOperations.search(criteriaQuery, Task.class)
                .map(SearchHit::getContent)
                .toList();


    }

}
