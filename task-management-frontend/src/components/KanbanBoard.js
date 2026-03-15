import React, { useEffect, useState } from 'react';
import { taskService } from '../services/api';
import './KanbanBoard.css';

function KanbanBoard({ tasks, setTasks, onTaskUpdated }) {
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchTasks();
  }, []);

  const fetchTasks = async () => {
    try {
      const response = await taskService.getAllTasks();
      setTasks(response.data);
    } catch (error) {
      console.error('Error fetching tasks:', error);
    } finally {
      setLoading(false);
    }
  };

  const getTasksByStatus = (status) => {
    return tasks.filter((task) => task.status === status);
  };

  const handleDragStart = (e, task) => {
    e.dataTransfer.effectAllowed = 'move';
    e.dataTransfer.setData('taskId', task.id);
  };

  const handleDragOver = (e) => {
    e.preventDefault();
    e.dataTransfer.dropEffect = 'move';
  };

  const handleDrop = async (e, newStatus) => {
    e.preventDefault();
    const taskId = parseInt(e.dataTransfer.getData('taskId'));

    const task = tasks.find((t) => t.id === taskId);
    if (task) {
      try {
        const updateData = {
          ...task,
          status: newStatus,
        };
        const response = await taskService.updateTask(taskId, updateData);
        onTaskUpdated(response.data);
      } catch (error) {
        console.error('Error updating task:', error);
      }
    }
  };

  if (loading) {
    return <div>Loading...</div>;
  }

  const todoTasks = getTasksByStatus('TODO');
  const inProgressTasks = getTasksByStatus('IN_PROGRESS');
  const doneTasks = getTasksByStatus('DONE');

  return (
    <div className="kanban-board">
      <div
        className="kanban-column"
        onDragOver={handleDragOver}
        onDrop={(e) => handleDrop(e, 'TODO')}
      >
        <h3>To Do ({todoTasks.length})</h3>
        <div className="task-cards">
          {todoTasks.map((task) => (
            <div
              key={task.id}
              className="kanban-card"
              draggable
              onDragStart={(e) => handleDragStart(e, task)}
            >
              <h5>{task.title}</h5>
              <p>{task.description}</p>
              <div className="card-footer">
                <span className={`priority ${task.priority.toLowerCase()}`}>
                  {task.priority}
                </span>
              </div>
            </div>
          ))}
        </div>
      </div>

      <div
        className="kanban-column"
        onDragOver={handleDragOver}
        onDrop={(e) => handleDrop(e, 'IN_PROGRESS')}
      >
        <h3>In Progress ({inProgressTasks.length})</h3>
        <div className="task-cards">
          {inProgressTasks.map((task) => (
            <div
              key={task.id}
              className="kanban-card in-progress"
              draggable
              onDragStart={(e) => handleDragStart(e, task)}
            >
              <h5>{task.title}</h5>
              <p>{task.description}</p>
              <div className="card-footer">
                <span className={`priority ${task.priority.toLowerCase()}`}>
                  {task.priority}
                </span>
              </div>
            </div>
          ))}
        </div>
      </div>

      <div
        className="kanban-column"
        onDragOver={handleDragOver}
        onDrop={(e) => handleDrop(e, 'DONE')}
      >
        <h3>Done ({doneTasks.length})</h3>
        <div className="task-cards">
          {doneTasks.map((task) => (
            <div
              key={task.id}
              className="kanban-card done"
              draggable
              onDragStart={(e) => handleDragStart(e, task)}
            >
              <h5>{task.title}</h5>
              <p>{task.description}</p>
              <div className="card-footer">
                <span className={`priority ${task.priority.toLowerCase()}`}>
                  {task.priority}
                </span>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

export default KanbanBoard;
