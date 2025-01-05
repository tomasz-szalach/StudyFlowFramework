document.addEventListener("DOMContentLoaded", function() {
    const taskContainer = document.querySelector(".task-display");

    function addTaskEventListeners(taskItem) {
        const checkbox = taskItem.querySelector(".task-checkbox");
        const deleteButton = taskItem.querySelector(".delete-task-button");

        checkbox.addEventListener("click", function() {
            const taskId = taskItem.getAttribute("data-task-id");
            const newStatus = checkbox.checked ? "completed" : "to_do";
            updateTaskStatus(taskId, newStatus);
        });

        deleteButton.addEventListener("click", function() {
            const taskId = taskItem.getAttribute("data-task-id");
            deleteTask(taskId);
        });
    }

    function addCheckboxListeners() {
        const taskItems = document.querySelectorAll(".task-item");

        taskItems.forEach(taskItem => {
            addTaskEventListeners(taskItem);
        });
    }

    function updateTaskStatus(taskId, newStatus) {
        fetch(`/updateTaskStatus?id=${taskId}`, {
            method: "POST",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ status: newStatus })
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                const taskItem = document.querySelector(`.task-item[data-task-id="${taskId}"]`);
                const taskName = taskItem.querySelector(".name");
                taskName.classList.toggle("completed", newStatus === "completed");
                taskItem.querySelector(".status").textContent = newStatus === "completed" ? 'Zakończone' : 'Do zrobienia';
            } else {
                console.error("Failed to update task status");
            }
        })
        .catch(error => console.error("Error updating task status:", error));
    }

    function deleteTask(taskId) {
        if (confirm("Czy na pewno chcesz usunąć to zadanie?")) {
            fetch(`/deleteTask?id=${taskId}`, {
                method: "POST"
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    const taskItem = document.querySelector(`.task-item[data-task-id="${taskId}"]`);
                    if (taskItem) {
                        taskItem.remove();
                    }
                } else {
                    console.error("Failed to delete task");
                }
            })
            .catch(error => console.error("Error deleting task:", error));
        }
    }

    // Dodaj nasłuchy dla istniejących elementów zadań
    addCheckboxListeners();

    // Dodaj nasłuchy dla przyszłych elementów zadań (po załadowaniu nowej listy zadań)
    const observer = new MutationObserver(() => {
        addCheckboxListeners();
    });

    observer.observe(taskContainer, { childList: true });
});
