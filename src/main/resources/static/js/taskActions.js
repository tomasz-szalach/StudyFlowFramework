document.addEventListener("DOMContentLoaded", function () {
    const taskLists = document.getElementById('task-lists');
    const tasks = document.getElementById('tasks');

    window.showTasks = function (taskListId) {
        fetch(`/api/tasks?task_list_id=${taskListId}`)
            .then(response => response.json())
            .then(data => {
                tasks.innerHTML = '';
                data.forEach(task => {
                    const taskElement = document.createElement('li');
                    taskElement.textContent = task.name;
                    tasks.appendChild(taskElement);
                });
            })
            .catch(error => console.error('Error fetching tasks:', error));
    };

    window.addTaskList = function () {
        const name = prompt("Enter the name of the new task list:");
        if (name) {
            fetch('/api/task_lists', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ name }),
            })
                .then(response => response.json())
                .then(data => {
                    const taskListElement = document.createElement('li');
                    taskListElement.textContent = data.name;
                    taskListElement.onclick = () => showTasks(data.id);
                    taskLists.appendChild(taskListElement);
                })
                .catch(error => console.error('Error adding task list:', error));
        }
    };

    window.addTask = function () {
        const name = prompt("Enter the name of the new task:");
        const description = prompt("Enter the description of the new task (optional):");
        const dueDate = prompt("Enter the due date of the new task (YYYY-MM-DD):");
        const status = prompt("Enter the status of the new task (to_do or completed):");
        const taskListId = prompt("Enter the ID of the task list to add this task to:");

        if (name && dueDate && status && taskListId) {
            fetch('/api/tasks', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ name, description, dueDate, status, taskListId }),
            })
                .then(response => response.json())
                .then(data => {
                    const taskElement = document.createElement('li');
                    taskElement.textContent = data.name;
                    tasks.appendChild(taskElement);
                })
                .catch(error => console.error('Error adding task:', error));
        }
    };
});
