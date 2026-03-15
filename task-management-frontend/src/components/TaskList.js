import React, { useEffect, useState } from 'react';
import { taskService } from '../services/api';
import TaskItem from './TaskItem';
import './TaskList.css';

function TaskList({ tasks, setTasks, onTaskDeleted, onTaskUpdated }) {
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

  if (loading) {
    return <div className="task-list-container"><p>Loading tasks...</p></div>;
  }

  return (
    <div className="task-list-container">
      <div className="task-list-header">
        <h3>All Tasks ({tasks.length})</h3>
      </div>
      {tasks.length === 0 ? (
        <p className="no-tasks">No tasks found. Create one to get started!</p>
      ) : (
        <div className="task-list">
          {tasks.map((task) => (
            <TaskItem
              key={task.id}
              task={task}
              onDeleted={onTaskDeleted}
              onUpdated={onTaskUpdated}
            />
          ))}
        </div>
      )}
    </div>
  );
}

export default TaskList;
