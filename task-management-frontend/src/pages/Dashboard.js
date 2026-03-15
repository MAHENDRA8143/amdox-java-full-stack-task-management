import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';
import TaskList from '../components/TaskList';
import TaskForm from '../components/TaskForm';
import KanbanBoard from '../components/KanbanBoard';
import './Dashboard.css';

function Dashboard() {
  const [tasks, setTasks] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [viewMode, setViewMode] = useState('list');
  const navigate = useNavigate();

  const token = localStorage.getItem('token');
  if (!token) {
    navigate('/login');
    return null;
  }

  const handleAddTask = (newTask) => {
    setTasks([...tasks, newTask]);
    setShowForm(false);
  };

  const handleDeleteTask = (taskId) => {
    setTasks(tasks.filter((task) => task.id !== taskId));
  };

  const handleUpdateTask = (updatedTask) => {
    setTasks(
      tasks.map((task) => (task.id === updatedTask.id ? updatedTask : task))
    );
  };

  return (
    <div className="dashboard">
      <Navbar />
      <div className="dashboard-content">
        <div className="dashboard-header">
          <h2>Task Management Dashboard</h2>
          <div className="view-controls">
            <button
              className={`btn ${viewMode === 'list' ? 'btn-primary' : 'btn-secondary'}`}
              onClick={() => setViewMode('list')}
            >
              List View
            </button>
            <button
              className={`btn ${viewMode === 'kanban' ? 'btn-primary' : 'btn-secondary'}`}
              onClick={() => setViewMode('kanban')}
            >
              Kanban Board
            </button>
            <button
              className="btn btn-success"
              onClick={() => setShowForm(!showForm)}
            >
              {showForm ? 'Close Form' : 'Create Task'}
            </button>
          </div>
        </div>

        {showForm && (
          <TaskForm onTaskCreated={handleAddTask} onCancel={() => setShowForm(false)} />
        )}

        {viewMode === 'list' && (
          <TaskList
            tasks={tasks}
            setTasks={setTasks}
            onTaskDeleted={handleDeleteTask}
            onTaskUpdated={handleUpdateTask}
          />
        )}

        {viewMode === 'kanban' && (
          <KanbanBoard
            tasks={tasks}
            setTasks={setTasks}
            onTaskUpdated={handleUpdateTask}
          />
        )}
      </div>
    </div>
  );
}

export default Dashboard;
