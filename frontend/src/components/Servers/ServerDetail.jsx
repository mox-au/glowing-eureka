import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card, Descriptions, Button, Tag, Space, message, Spin } from 'antd';
import { ArrowLeftOutlined, SyncOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import serverService from '../../services/serverService';
import { POLL_STATUS_COLORS } from '../../utils/constants';

const ServerDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [server, setServer] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadServer();
  }, [id]);

  const loadServer = async () => {
    setLoading(true);
    try {
      const data = await serverService.getServerById(id);
      setServer(data);
    } catch (error) {
      message.error('Failed to load server details');
    } finally {
      setLoading(false);
    }
  };

  const handlePoll = async () => {
    try {
      await serverService.pollServer(id);
      message.success('Polling started');
      setTimeout(loadServer, 2000);
    } catch (error) {
      message.error('Failed to start polling');
    }
  };

  if (loading) {
    return <Spin size="large" />;
  }

  if (!server) {
    return <div>Server not found</div>;
  }

  return (
    <div>
      <Space style={{ marginBottom: 16 }}>
        <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/servers')}>
          Back to Servers
        </Button>
        <Button icon={<SyncOutlined />} onClick={handlePoll}>
          Poll Now
        </Button>
      </Space>

      <Card title={`Server: ${server.serverName}`}>
        <Descriptions bordered column={2}>
          <Descriptions.Item label="Server Name">{server.serverName}</Descriptions.Item>
          <Descriptions.Item label="Base URL">{server.baseUrl}</Descriptions.Item>
          <Descriptions.Item label="Debtor Code">{server.prontoDebtorCode}</Descriptions.Item>
          <Descriptions.Item label="Xi Version">{server.prontoXiVersion}</Descriptions.Item>
          <Descriptions.Item label="Status">
            <Tag color={POLL_STATUS_COLORS[server.pollStatus] || 'default'}>
              {server.pollStatus}
            </Tag>
          </Descriptions.Item>
          <Descriptions.Item label="Active">
            <Tag color={server.isActive ? 'green' : 'red'}>
              {server.isActive ? 'Yes' : 'No'}
            </Tag>
          </Descriptions.Item>
          <Descriptions.Item label="Enrolled By">{server.enrolledBy || 'N/A'}</Descriptions.Item>
          <Descriptions.Item label="Enrollment Date">
            {server.enrollmentDate ? dayjs(server.enrollmentDate).format('YYYY-MM-DD HH:mm:ss') : 'N/A'}
          </Descriptions.Item>
          <Descriptions.Item label="Last Poll Time">
            {server.lastPollTime ? dayjs(server.lastPollTime).format('YYYY-MM-DD HH:mm:ss') : 'Never'}
          </Descriptions.Item>
          <Descriptions.Item label="Last Error" span={2}>
            {server.lastError || 'None'}
          </Descriptions.Item>
        </Descriptions>
      </Card>
    </div>
  );
};

export default ServerDetail;
