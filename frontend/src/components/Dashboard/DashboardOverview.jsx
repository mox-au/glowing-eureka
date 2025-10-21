import React, { useState, useEffect } from 'react';
import { Row, Col, Card, Statistic, Spin, message } from 'antd';
import { DatabaseOutlined, CheckCircleOutlined, CloseCircleOutlined, ClockCircleOutlined } from '@ant-design/icons';
import serverService from '../../services/serverService';

const DashboardOverview = () => {
  const [loading, setLoading] = useState(true);
  const [servers, setServers] = useState([]);

  useEffect(() => {
    loadServers();
  }, []);

  const loadServers = async () => {
    try {
      const data = await serverService.getAllServers();
      setServers(data);
    } catch (error) {
      message.error('Failed to load servers');
    } finally {
      setLoading(false);
    }
  };

  const stats = {
    total: servers.length,
    active: servers.filter(s => s.isActive).length,
    success: servers.filter(s => s.pollStatus === 'SUCCESS').length,
    failed: servers.filter(s => s.pollStatus === 'FAILED').length,
    neverPolled: servers.filter(s => s.pollStatus === 'NEVER_POLLED').length,
  };

  if (loading) {
    return <Spin size="large" />;
  }

  return (
    <div>
      <h1>Dashboard Overview</h1>
      <Row gutter={16}>
        <Col span={6}>
          <Card>
            <Statistic
              title="Total Servers"
              value={stats.total}
              prefix={<DatabaseOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="Active Servers"
              value={stats.active}
              valueStyle={{ color: '#3f8600' }}
              prefix={<CheckCircleOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="Last Poll Success"
              value={stats.success}
              valueStyle={{ color: '#52c41a' }}
              prefix={<CheckCircleOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="Last Poll Failed"
              value={stats.failed}
              valueStyle={{ color: '#cf1322' }}
              prefix={<CloseCircleOutlined />}
            />
          </Card>
        </Col>
      </Row>
      <Row gutter={16} style={{ marginTop: 16 }}>
        <Col span={6}>
          <Card>
            <Statistic
              title="Never Polled"
              value={stats.neverPolled}
              valueStyle={{ color: '#999' }}
              prefix={<ClockCircleOutlined />}
            />
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default DashboardOverview;
