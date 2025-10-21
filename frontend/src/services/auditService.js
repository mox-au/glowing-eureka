import api from './api';

const auditService = {
  getAuditLogs: async (filters = {}) => {
    const params = new URLSearchParams();
    if (filters.userId) params.append('userId', filters.userId);
    if (filters.action) params.append('action', filters.action);
    if (filters.startDate) params.append('startDate', filters.startDate);
    if (filters.endDate) params.append('endDate', filters.endDate);

    const response = await api.get(`/audit/logs?${params.toString()}`);
    return response.data;
  },
};

export default auditService;
