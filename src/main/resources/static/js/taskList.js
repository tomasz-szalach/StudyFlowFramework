document.addEventListener("DOMContentLoaded", function() {
    const taskLists = document.querySelectorAll(".TASKLIST");
    const taskContainer = document.querySelector(".task-display");

    taskLists.forEach(taskList => {
        taskList.addEventListener("click", function(event) {
            event.preventDefault();
            const taskListId = this.getAttribute("data-tasklist-id");
            fetchTasks(taskListId);
        });
    });

    function fetchTasks(taskListId) {
        fetch(`/getTasks?id=${taskListId}`)
            .then(response => response.json())
            .then(data => {
                const tasks = data.tasks;
                taskContainer.innerHTML = "";
                loadTasks(tasks);
            });
    }

    function loadTasks(tasks) {
        tasks.forEach(task => {
            createTask(task);
        });

        const taskItems = document.querySelectorAll(".task-item");
        taskItems.forEach(taskItem => {
            taskItem.querySelector(".task-checkbox").addEventListener("click", function () {
                const taskId = taskItem.getAttribute("data-task-id");
                const newStatus = this.checked ? "completed" : "to_do";
                updateTaskStatus(taskId, newStatus);
            });

            const deleteButton = taskItem.querySelector(".delete-task-button");
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
        clone.querySelector(".status").innerText = task.status === "completed" ? "Zakończone" : "Do zrobienia";
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
                    taskItem.querySelector(".status").innerText = newStatus === "completed" ? "Zakończone" : "Do zrobienia";
                }
            }
        })
        .catch(error => console.error("Error updating task status:", error));
    }

    function deleteTask(taskId) {
        fetch(`/deleteTask?id=${taskId}`, {
            method: "POST",
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(response => response.json())
        .then(data => {
            if (!data.success) {
                console.error("Failed to delete task");
            } else {
                const taskItem = document.querySelector(`.task-item[data-task-id="${taskId}"]`);
                if (taskItem) {
                    taskItem.remove();
                }
            }
        })
        .catch(error => console.error("Error deleting task:", error));
    }
});
