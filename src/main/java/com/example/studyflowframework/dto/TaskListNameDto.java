package com.example.studyflowframework.dto;

public record TaskListNameDto(String name) {
    public String trimmed() {
        return name == null ? "" : name.trim();
    }
}
