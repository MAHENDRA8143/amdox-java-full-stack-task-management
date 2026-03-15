import axios from 'axios';

const API_URL = "http://localhost:8080/api";

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
});

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

api.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('API Error:', error.response?.data || error.message);

    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }

    return Promise.reject(error);
  }
);

export const authService = {
  register: (name, email, password, role = 'USER') =>
    api.post('/auth/register', { name, email, password, role }),

  login: (email, password) =>
    api.post('/auth/login', { email, password }),
};

export const taskService = {
  getAllTasks: () => api.get('/tasks'),
  getTaskById: (id) => api.get(`/tasks/${id}`),
  createTask: (data) => api.post('/tasks', data),
  updateTask: (id, data) => api.put(`/tasks/${id}`, data),
  deleteTask: (id) => api.delete(`/tasks/${id}`),
  getTasksByStatus: (status) => api.get(`/tasks/status/${status}`),
  getTasksByAssignedUser: (userId) => api.get(`/tasks/assigned/${userId}`),
  getOverdueTasks: () => api.get('/tasks/overdue'),
  getTasksByPriority: (priority) => api.get(`/tasks/priority/${priority}`),
};

export const commentService = {
  getCommentsByTask: (taskId) => api.get(`/tasks/${taskId}/comments`),
  addComment: (taskId, message) => api.post(`/tasks/${taskId}/comments`, { message }),
  deleteComment: (taskId, commentId) => api.delete(`/tasks/${taskId}/comments/${commentId}`),
};

export const reportService = {
  getBurndown: (filters = {}) => api.get('/reports/burndown', { params: filters }),
  getVelocity: (filters = {}) => api.get('/reports/velocity', { params: filters }),
  getTaskSummary: (filters = {}) => api.get('/reports/task-summary', { params: filters }),
  getWorkload: (filters = {}) => api.get('/reports/workload', { params: filters }),
  getCumulativeFlow: (filters = {}) => api.get('/reports/cumulative-flow', { params: filters }),
  getOverview: (filters = {}) => api.get('/reports/overview', { params: filters }),
};

export const integrationService = {
  processGitHubEvent: (event, payload) =>
    api.post('/integrations/github', payload, {
      headers: { 'X-GitHub-Event': event },
    }),
  processJenkinsEvent: (payload) => api.post('/integrations/jenkins', payload),
  processDockerEvent: (payload) => api.post('/integrations/docker', payload),
  handleCommit: (message, author) =>
    api.post('/integrations/commit', null, {
      params: { message, author },
    }),
  handlePullRequest: (title, author) =>
    api.post('/integrations/pull-request', null, {
      params: { title, author },
    }),
};

export default api;
