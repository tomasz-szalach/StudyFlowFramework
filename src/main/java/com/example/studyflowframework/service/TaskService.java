package com.example.studyflowframework.service;

import com.example.studyflowframework.model.Task;
import com.example.studyflowframework.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class TaskService {

    private final TaskRepository repo;
    @Autowired
    public TaskService(TaskRepository repo){ this.repo = repo; }

    /* ========== dodawanie ========== */
    @Transactional
    public void addTask(String name,String desc,String due,
                        Short statusId,Long listId,Long reporterId,
                        Short priorityId){
        Task t = new Task(name,desc,LocalDate.parse(due),
                reporterId,listId,priorityId,statusId);
        repo.save(t);
    }

    /* ========== pobieranie ========== */
    public List<Task> getTasksByUserId(Long uid){      // dla AccountController
        return repo.findByReporterId(uid);
    }
    public List<Task> getTasksByTaskList(Long listId, Long uid){
        return repo.findByTaskListIdAndReporterIdOrderByDueDateAsc(listId,uid);
    }
    public List<Task> searchTasksInList(Long listId, Long uid, String q){
        return repo.searchTasks(q==null?"":q, listId, uid);
    }
    public Task getTaskById(Long id){ return repo.findById(id).orElse(null); }

    /* ========== status ========== */
    private static final Map<String,Short> STATUS =
            Map.of("todo",(short)1,"in_progress",(short)2,
                    "completed",(short)3,"archived",(short)4);

    @Transactional
    public void updateTaskStatus(Long id, String code){
        repo.updateTaskStatus(id, STATUS.getOrDefault(code,(short)1));
    }

    /* ========== save / delete ========== */
    @Transactional public Task saveTask(Task t){ return repo.save(t); }
    @Transactional public void deleteTask(Long id){ repo.deleteById(id); }
}
