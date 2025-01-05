const search = document.querySelector('input[placeholder="Znajdź zadanie"]');
const taskContainer = document.querySelector(".task-display");
let taskListSelect = document.querySelector('.TASKLIST.selected');

search.addEventListener("keyup", function (event) {
    if (event.key === "Enter") {
        event.preventDefault();

        taskListSelect = document.querySelector('.TASKLIST.selected');

        const data = {
            query: this.value,
            task_list_id: taskListSelect ? taskListSelect.getAttribute("data-tasklist-id") : null
        };

        console.log("Sending data:", data);
        fetch("/searchTasks", {
            method: "POST",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        }).then(function (response) {
            return response.json();
        }).then(function (response) {
            console.log("Received response:", response);
            const tasks = response.tasks;
            taskContainer.innerHTML = "";
            loadTasks(tasks);
        });
    }
});

function loadTasks(tasks) {
    tasks.forEach(task => {
        createTask(task);
    });

    // Dodaj obsługę zdarzeń dla checkboxów i przycisków usuwania
    const taskItems = document.querySelectorAll(".task-item");
    taskItems.forEach(taskItem => {
        const checkbox = taskItem.querySelector(".task-checkbox");
        const deleteButton = taskItem.querySelector(".delete-task-button");

        checkbox.addEventListener("click", function () {
            const taskId = taskItem.getAttribute("data-task-id");
            const newStatus = this.checked ? "completed" : "to_do";
            updateTaskStatus(taskId, newStatus);
        });

        deleteButton.addEventListener("click", function () {
            const taskId = taskItem.getAttribute("data-task-id");
            deleteTask(taskId);
        });
    });
}

function createTask(task) {
    const template = document.querySelector("#task-template");
    const clone = template.content.cloneNode(true);
    clone.querySelector(".task-item").setAttribute("data-task-id", task.id);
    const taskName = clone.querySelector(".name");
    taskName.innerText = task.name;
    if (task.status === "completed") {
        taskName.classList.add("completed");
    } else {
        taskName.classList.remove("completed");
    }
    clone.querySelector(".due-date").innerText = task.due_date;
    clone.querySelector(".description").innerText = task.description;
    const checkbox = clone.querySelector(".task-checkbox");
    checkbox.checked = task.status === "completed";
    clone.querySelector(".status").innerText = task.status === "completed" ? 'Zakończone' : 'Do zrobienia';
    taskContainer.appendChild(clone);
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
        if (!data.success) {
            console.error("Failed to update task status");
        } else {
            const taskItem = document.querySelector(`.task-item[data-task-id="${taskId}"]`);
            if (taskItem) {
                const taskName = taskItem.querySelector(".name");
                if (newStatus === "completed") {
                    taskName.classList.add("completed");
                } else {
                    taskName.classList.remove("completed");
                }
                taskItem.querySelector(".status").innerText = newStatus === "completed" ? 'Zakończone' : 'Do zrobienia';
            }
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
