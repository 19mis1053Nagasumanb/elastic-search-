package com.example.taskmangementsystem.g.controller;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.example.taskmangementsystem.g.entity.Task;
import com.example.taskmangementsystem.g.service.ESService;
import com.example.taskmangementsystem.g.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/apis")
@CrossOrigin(origins = "http://localhost:4200")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @Autowired
    private ESService esService;

    @GetMapping("/findAll")
    Iterable<Task>getAllTasks(){
        return taskService.getTask();
    }

//    @PostMapping("/insert")
//    Task insertTask(@RequestBody Task task){
//        System.out.println("entering into insert service"+task);
//        return taskService.insertTask(task);
//    }

    @PostMapping("/insert")
    public ResponseEntity<?> createTask(@RequestBody Task task) {
        try {
            taskService.insertTask(task);
//            return ResponseEntity.ok("Task created successfully");
            return ResponseEntity.ok(Map.of("message", "Task created successfully")); // Returning a JSON object

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/all")
    public ResponseEntity<String> deleteAllTasks(){
        System.out.println("deleting all data");
        taskService.deleteAllTasks();
        return ResponseEntity.ok("All tasks deleted successfully");
    }

//    @DeleteMapping("/{id}")
//    public String deleteTaskById(@PathVariable String id){
//        System.out.println("Deleting task");
//
//        taskService.deleteTask(id);
//        return "Task with ID"+" "+id+" "+"deleted successfully";
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteTaskById(@PathVariable String id) {
        System.out.println("Deleting task");
        taskService.deleteTask(id);
        Map<String, String> response = Map.of("message", "Task with ID " + id + " deleted successfully");
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}")
    public Task getTasksById(@PathVariable String id){
        System.out.println("Getting task by id");
        return taskService.getTaskById(id);
    }

    @PutMapping("/{id}")
    public Task updateTasksById(@RequestBody Task task,@PathVariable String id){
        System.out.println("updating task by id"+id);
        return taskService.updateTask(task,id);
    }


    @GetMapping("/fuzzySearch/{approximateTaskName}")
    public List<Task> fuzzySearch(@PathVariable String approximateTaskName, @RequestParam(defaultValue="AUTO") String fuzziness) throws IOException {
        SearchResponse<Task> searchResponse = esService.fuzzySearch(approximateTaskName,fuzziness);
        List<Hit<Task>>hitList= searchResponse.hits().hits();
        System.out.println(hitList);
        List<Task>taskList = new ArrayList<>();
        for(Hit<Task> hit: hitList){
            taskList.add(hit.source());
        }
        return taskList;
    }

//    @GetMapping("/searchWithSorting")
//    public List<Task> searchWithSorting(
//            @RequestParam String searchTerm,
//            @RequestParam String sortField,
//            @RequestParam String sortOrder,
//            @RequestParam(defaultValue = "AUTO") String fuzziness) throws IOException {
//
//        SearchResponse<Task> searchResponse = esService.searchWithSorting(searchTerm, sortField, sortOrder, fuzziness);
//        List<Hit<Task>> hitList = searchResponse.hits().hits();
//        List<Task> taskList = new ArrayList<>();
//        for (Hit<Task> hit : hitList) {
//            taskList.add(hit.source());
//        }
//        return taskList;
//    }

//    @GetMapping("/searchWithSorting")
//    public ResponseEntity<?> searchWithSorting(
//            @RequestParam String searchTerm,
//            @RequestParam String sortField,
//            @RequestParam String sortOrder,
//            @RequestParam String fuzziness) {
//        try {
//            return ResponseEntity.ok(esService.searchWithSorting(searchTerm, sortField, sortOrder, fuzziness));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
//        }
//    }




    @GetMapping("/autoSuggest/{field}/{searchTerm}")
    public List<String> autoSuggestTaskSearch(@PathVariable String field, @PathVariable String searchTerm) throws IOException {
        SearchResponse<Task> searchResponse = esService.autoSuggestTask(searchTerm, field);
        List<Hit<Task>> hitList = searchResponse.hits().hits();
        List<Task> taskList = new ArrayList<>();
        for (Hit<Task> hit : hitList) {
            taskList.add(hit.source());
        }

        // Collect the matched tasks based on the field
        List<String> result = new ArrayList<>();
        for (Task task : taskList) {
            switch (field) {
                case "name":
                    result.add(task.getName());
                    break;
                case "task":
                    result.add(task.getTask()); // Fetch the task description
                    break;
                case "priority":
                    result.add(task.getPriority().toString()); // Convert Enum to string
                    break;
                case "logHours":
                    result.add(task.getLogHours()); // Log hours field
                    break;
                case "day":
                    result.add(task.getDay());
                    break;
                case "status":
                    result.add(task.getStatus().toString()); // Convert Enum to string
                    break;
                case "date":
                    result.add(task.getFormattedDate()); // Use formatted date
                    break;

                default:
                    result.add("Invalid field");
            }
        }
        return result;
    }


    @GetMapping("/search")
    public List<Task>search(@RequestParam String query){

        return taskService.searchByQuery(query);
    }
}
