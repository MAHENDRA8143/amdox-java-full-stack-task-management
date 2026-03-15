import React, { useState } from 'react';
import { taskService } from '../services/api';
import './TaskItem.css';

function TaskItem({ task, onDeleted, onUpdated }) {
  const [isEditing, setIsEditing] = useState(false);
  const [editData, setEditData] = useState(task);
  const [loading, setLoading] = useState(false);

  const getPriorityColor = (priority) => {
    switch (priority) {
      case 'HIGH':
        return '#dc3545';
      case 'MEDIUM':
        return '#ffc107';
      case 'LOW':
        return '#28a745';
      default:
        return '#6c757d';
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'TODO':
        return '#6c757d';
      case 'IN_PROGRESS':
        return '#0d6efd';
      case 'DONE':
        return '#28a745';
      default:
        return '#6c757d';
    }
  };

  const handleDelete = async () => {
    if (window.confirm('Are you sure you want to delete this task?')) {
      try {
        await taskService.deleteTask(task.id);
        onDeleted(task.id);
      } catch (error) {
        alert('Failed to delete task');
      }
    }
  };

  const handleUpdate = async () => {
    setLoading(true);
    try {
      const response = await taskService.updateTask(task.id, editData);
      onUpdated(response.data);
      setIsEditing(false);
    } catch (error) {
      alert('Failed to update task');
    } finally {
      setLoading(false);
    }
  };

  const handleEditChange = (e) => {
    const { name, value } = e.target;
    setEditData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  if (isEditing) {
    return (
      <div className="task-item editing">
        <div className="edit-form">
          <input
            type="text"
            name="title"
            value={editData.title}
            onChange={handleEditChange}
            className="form-control"
            placeholder="Task Title"
          />
          <textarea
            name="description"
            value={editData.description || ''}
            onChange={handleEditChange}
            className="form-control"
            placeholder="Task Description"
            rows="3"
          ></textarea>
          <div className="edit-row">
            <select
              name="priority"
              value={editData.priority}
              onChange={handleEditChange}
              className="form-control"
            >
              <option value="LOW">Low</option>
              <option value="MEDIUM">Medium</option>
              <option value="HIGH">High</option>
            </select>
            <select
              name="status"
              value={editData.status}
              onChange={handleEditChange}
              className="form-control"
            >
              <option value="TODO">To Do</option>
              <option value="IN_PROGRESS">In Progress</option>
              <option value="DONE">Done</option>
            </select>
          </div>
          <div className="edit-actions">
            <button
              className="btn btn-success"
              onClick={handleUpdate}
              disabled={loading}
            >
              {loading ? 'Saving...' : 'Save'}
            </button>
            <button
              className="btn btn-secondary"
              onClick={() => setIsEditing(false)}
            >
              Cancel
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="task-item">
      <div className="task-header">
        <div className="task-title-area">
          <h4>{task.title}</h4>
          <div className="task-badges">
            <span
              className="badge"
              style={{ backgroundColor: getPriorityColor(task.priority) }}
            >
              {task.priority}
            </span>
            <span
              className="badge"
              style={{ backgroundColor: getStatusColor(task.status) }}
            >
              {task.status}
            </span>
          </div>
        </div>
        <div className="task-actions">
          <button
            className="btn btn-sm btn-warning"
            onClick={() => setIsEditing(true)}
          >
            Edit
          </button>
          <button
            className="btn btn-sm btn-danger"
            onClick={handleDelete}
          >
            Delete
          </button>
        </div>
      </div>
      <p className="task-description">{task.description}</p>
      {task.dueDate && (
        <p className="task-due-date">Due: {new Date(task.dueDate).toLocaleDateString()}</p>
      )}
    </div>
  );
}

export default TaskItem;
