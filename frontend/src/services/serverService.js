import api from './api';

const serverService = {
  getAllServers: async () => {
    const response = await api.get('/servers');
    return response.data;
  },

  getActiveServers: async () => {
    const response = await api.get('/servers?active=true');
    return response.data;
  },

  getServerById: async (id) => {
    const response = await api.get(`/servers/${id}`);
    return response.data;
  },

  enrollServer: async (serverData) => {
    const response = await api.post('/servers', serverData);
    return response.data;
  },

  deleteServer: async (id) => {
    const response = await api.delete(`/servers/${id}`);
    return response.data;
  },

  pollServer: async (id) => {
    const response = await api.post(`/servers/${id}/poll`);
    return response.data;
  },

  pollAllServers: async () => {
    const response = await api.post('/servers/poll-all');
    return response.data;
  },
};

export default serverService;
