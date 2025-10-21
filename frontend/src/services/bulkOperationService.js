import api from './api';

const bulkOperationService = {
  createBulkDeploy: async (deployData) => {
    const response = await api.post('/bulk-operations/deploy', deployData);
    return response.data;
  },

  getAllOperations: async () => {
    const response = await api.get('/bulk-operations');
    return response.data;
  },

  getOperationById: async (id) => {
    const response = await api.get(`/bulk-operations/${id}`);
    return response.data;
  },

  getOperationDetails: async (id) => {
    const response = await api.get(`/bulk-operations/${id}/details`);
    return response.data;
  },
};

export default bulkOperationService;
