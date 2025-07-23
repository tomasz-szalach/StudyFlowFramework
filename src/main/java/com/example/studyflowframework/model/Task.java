package com.example.studyflowframework.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Map;

/* ------------------------------------------------------------
   SŁOWNIKI  (ID ↔ CODE)  ─ identyczne z tabelami pomocniczymi
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
                    .collect(java.util.stream.Collectors.toMap(
                            Map.Entry::getValue, Map.Entry::getKey));

    static final Map<Short,String> PRIORITY = Map.of(
            (short)1, "LOW",
            (short)2, "MEDIUM",
            (short)3, "HIGH"
    );
    static final Map<String,Short> PRIORITY_INV =
            PRIORITY.entrySet().stream()
                    .collect(java.util.stream.Collectors.toMap(
                            Map.Entry::getValue, Map.Entry::getKey));
}

@Entity @Table(name="tasks")
public class Task {

    /* ===== kolumny ===== */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="task_id")       private Long id;
    @Column(name="task_name")     private String name;
    @Column(length = 1000)        private String description;
    @Column(name="due_date")      private LocalDate dueDate;

    @Column(name="reporter_id")   private Long   reporterId;
    @Column(name="task_list_id")  private Long   taskListId;
    @Column(name="priority_id")   private Short  priorityId;
    @Column(name="status_id")     private Short  statusId;

    /* ===== konstruktory ===== */
    public Task() {}             // JPA
    public Task(String name, String description, LocalDate due,
                Long reporterId, Long taskListId,
                Short priorityId, Short statusId) {
        this.name        = name;
        this.description = description;
        this.dueDate     = due;
        this.reporterId  = reporterId;
        this.taskListId  = taskListId;
        this.priorityId  = priorityId;
        this.statusId    = statusId;
    }

    /* ===== get / set ===== */
    public Long getId(){return id;}

    public String getName(){return name;}
    public void   setName(String n){this.name=n;}

    public String getDescription(){return description;}
    public void   setDescription(String d){this.description=d;}

    public LocalDate getDueDate(){return dueDate;}
    public void      setDueDate(LocalDate d){this.dueDate=d;}

    /* ► aliasy wsteczne (dawny userId) */
    public Long getUserId(){return reporterId;}
    public void setUserId(Long id){this.reporterId=id;}

    public Long getTaskListId(){return taskListId;}
    public void setTaskListId(Long id){this.taskListId=id;}

    /* --------  Priorytet  -------- */
    @Transient
    public String getPriority(){ return Dict.PRIORITY.get(priorityId); }
    public  void  setPriority(String code){
        this.priorityId = Dict.PRIORITY_INV.getOrDefault(code.toUpperCase(), (short)1);
    }
    public Short getPriorityId(){return priorityId;}
    public void  setPriorityId(Short id){this.priorityId=id;}

    /* --------  Status  -------- */
    @Transient
    public String getStatus(){ return Dict.STATUS.get(statusId); }
    public  void  setStatus(String code){
        this.statusId = Dict.STATUS_INV.getOrDefault(code.toLowerCase(), (short)1);
    }
    public Short getStatusId(){return statusId;}
    public void  setStatusId(Short id){this.statusId=id;}
}
