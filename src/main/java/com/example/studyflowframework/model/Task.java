package com.example.studyflowframework.model;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

/* ------------------------------------------------------------
   SŁOWNIKI  (ID ↔ CODE)
   ------------------------------------------------------------ */
final class Dict {
    static final Map<Short,String> STATUS = Map.of(
            (short)1, "todo",
            (short)2, "in_progress",
            (short)3, "completed",
            (short)4, "archived"
    );
    static final Map<String,Short> STATUS_INV =
            STATUS.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

    static final Map<Short,String> PRIORITY = Map.of(
            (short)1, "LOW",
            (short)2, "MEDIUM",
            (short)3, "HIGH"
    );
    static final Map<String,Short> PRIORITY_INV =
            PRIORITY.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
}

@Entity
@Table(name = "tasks")
@SQLDelete(sql = "UPDATE tasks SET deleted_at = CURRENT_TIMESTAMP WHERE task_id = ?")
@Where(clause = "deleted_at IS NULL")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Long id;

    @Column(name = "task_name")
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "reporter_id")
    private Long reporterId;

    @Column(name = "task_list_id")
    private Long taskListId;

    @Column(name = "priority_id")
    private Short priorityId;

    @Column(name = "status_id")
    private Short statusId;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public Task() {}

    public Task(String name, String description,
                LocalDate startDate, LocalDate dueDate,
                Long reporterId, Long taskListId,
                Short priorityId, Short statusId) {
        this.name        = name;
        this.description = description;
        this.startDate   = startDate;
        this.dueDate     = dueDate;
        this.reporterId  = reporterId;
        this.taskListId  = taskListId;
        this.priorityId  = priorityId;
        this.statusId    = statusId;
    }

    public Task(String name, String description, LocalDate dueDate,
                Long reporterId, Long taskListId,
                Short priorityId, Short statusId) {
        this(name, description, LocalDate.now(), dueDate,
                reporterId, taskListId, priorityId, statusId);
    }

    public Long getId() { return id; }

    public String getName() { return name; }
    public void   setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void   setDescription(String description) { this.description = description; }

    public LocalDate getStartDate() { return startDate; }
    public void      setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void      setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void          setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public Long getUserId() { return reporterId; }
    public void setUserId(Long reporterId) { this.reporterId = reporterId; }

    public Long getTaskListId() { return taskListId; }
    public void setTaskListId(Long taskListId) { this.taskListId = taskListId; }

    public String getPriority() { return Dict.PRIORITY.get(priorityId); }
    public void   setPriority(String code) {
        this.priorityId = Dict.PRIORITY_INV.getOrDefault(code.toUpperCase(), (short)1);
    }
    public Short getPriorityId() { return priorityId; }
    public void  setPriorityId(Short priorityId) { this.priorityId = priorityId; }

    public String getStatus() { return Dict.STATUS.get(statusId); }
    public void   setStatus(String code) {
        this.statusId = Dict.STATUS_INV.getOrDefault(code.toLowerCase(), (short)1);
    }
    public Short getStatusId() { return statusId; }
    public void  setStatusId(Short statusId) { this.statusId = statusId; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}
