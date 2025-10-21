import React, { useState, useEffect } from 'react';
import { Table, Button, Space, Tag, Input, message, Popconfirm } from 'antd';
import { SyncOutlined, EyeOutlined, PlusOutlined, DeleteOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import dayjs from 'dayjs';
import relativeTime from 'dayjs/plugin/relativeTime';
import serverService from '../../services/serverService';
import { POLL_STATUS_COLORS } from '../../utils/constants';
import { useAuth } from '../../context/AuthContext';

dayjs.extend(relativeTime);

const ServerList = () => {
  const [servers, setServers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [searchText, setSearchText] = useState('');
  const navigate = useNavigate();
  const { isAdmin } = useAuth();

  useEffect(() => {
    loadServers();
  }, []);

  const loadServers = async () => {
    setLoading(true);
    try {
      const data = await serverService.getAllServers();
      setServers(data);
    } catch (error) {
      message.error('Failed to load servers');
    } finally {
      setLoading(false);
    }
  };

  const handlePoll = async (id) => {
    try {
      await serverService.pollServer(id);
      message.success('Polling started');
      setTimeout(loadServers, 2000);
    } catch (error) {
      message.error('Failed to start polling');
    }
  };

  const handleDelete = async (id) => {
    try {
      await serverService.deleteServer(id);
      message.success('Server deleted');
      loadServers();
    } catch (error) {
      message.error('Failed to delete server');
    }
  };

  const columns = [
    {
      title: 'Server Name',
      dataIndex: 'serverName',
      key: 'serverName',
      filteredValue: searchText ? [searchText] : null,
      onFilter: (value, record) =>
        record.serverName.toLowerCase().includes(value.toLowerCase()) ||
        record.prontoDebtorCode.toLowerCase().includes(value.toLowerCase()),
    },
    {
      title: 'Debtor Code',
      dataIndex: 'prontoDebtorCode',
      key: 'prontoDebtorCode',
    },
    {
      title: 'Xi Version',
      dataIndex: 'prontoXiVersion',
      key: 'prontoXiVersion',
    },
    {
      title: 'Last Poll',
      dataIndex: 'lastPollTime',
      key: 'lastPollTime',
      render: (time) => time ? dayjs(time).fromNow() : 'Never',
    },
    {
      title: 'Status',
      dataIndex: 'pollStatus',
      key: 'pollStatus',
      render: (status) => (
        <Tag color={POLL_STATUS_COLORS[status] || 'default'}>
          {status}
        </Tag>
      ),
    },
    {
      title: 'Actions',
      key: 'actions',
      render: (_, record) => (
        <Space>
          <Button
            icon={<SyncOutlined />}
            size="small"
            onClick={() => handlePoll(record.id)}
          >
            Poll
          </Button>
          <Button
            icon={<EyeOutlined />}
            size="small"
            onClick={() => navigate(`/servers/${record.id}`)}
          >
            Details
          </Button>
          {isAdmin && (
            <Popconfirm
              title="Delete server?"
              onConfirm={() => handleDelete(record.id)}
            >
              <Button
                icon={<DeleteOutlined />}
                size="small"
                danger
              >
                Delete
              </Button>
            </Popconfirm>
          )}
        </Space>
      ),
    },
  ];

  return (
    <div>
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between' }}>
        <Input.Search
          placeholder="Search servers..."
          onChange={(e) => setSearchText(e.target.value)}
          style={{ width: 300 }}
        />
        <Space>
          <Button icon={<SyncOutlined />} onClick={loadServers}>
            Refresh
          </Button>
          {isAdmin && (
            <Button
              type="primary"
              icon={<PlusOutlined />}
              onClick={() => navigate('/servers/enroll')}
            >
              Enroll New Server
            </Button>
          )}
        </Space>
      </div>

      <Table
        columns={columns}
        dataSource={servers}
        loading={loading}
        rowKey="id"
      />
    </div>
  );
};

export default ServerList;
