document.addEventListener('DOMContentLoaded', function () {
    const taskContainer = document.querySelector(".task-display");

    taskContainer.addEventListener('click', function (event) {
        if (event.target.classList.contains('delete-task-button')) {
            const taskId = event.target.getAttribute('data-task-id');
            if (confirm('Czy na pewno chcesz usunąć to zadanie?')) {
                deleteTask(taskId);
            }
        }
    });

    function deleteTask(taskId) {
        fetch(`/deleteTask?id=${taskId}`, {
            method: 'POST',
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
