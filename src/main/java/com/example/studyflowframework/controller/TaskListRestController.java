package com.example.studyflowframework.controller;

import com.example.studyflowframework.model.TaskList;
import com.example.studyflowframework.service.TaskListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasklists")
@Tag(name = "Task List Management – REST")
public class TaskListRestController {

    private final TaskListService service;

    @Autowired
    public TaskListRestController(TaskListService service) {
        this.service = service;
    }

    /* ---------- PUT /api/tasklists/{id} ---------- */
    @PutMapping("/{id}")
    @Operation(summary = "Zmień nazwę listy zadań")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Nazwa zmieniona"),
            @ApiResponse(responseCode = "400", description = "Puste pole 'name'"),
            @ApiResponse(responseCode = "404", description = "Lista nie znaleziona")
    })
    public ResponseEntity<TaskList> rename(
            @PathVariable Long id,
            @RequestBody TaskList body) {

        String newName = body.getName() == null ? "" : body.getName().trim();
        if (newName.isBlank()) return ResponseEntity.badRequest().build();

        try {
            TaskList updated = service.rename(id, newName);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /* ---------- DELETE /api/tasklists/{id} ---------- */
    @DeleteMapping("/{id}")
    @Operation(summary = "Usuń listę zadań")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usunięto"),
            @ApiResponse(responseCode = "404", description = "Lista nie znaleziona")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            service.delete(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
