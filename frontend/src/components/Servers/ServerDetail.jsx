import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card, Descriptions, Button, Tag, Space, message, Spin, Modal, Form, Input } from 'antd';
import { ArrowLeftOutlined, SyncOutlined, EditOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import serverService from '../../services/serverService';
import { POLL_STATUS_COLORS } from '../../utils/constants';

const ServerDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [server, setServer] = useState(null);
  const [loading, setLoading] = useState(true);
  const [editModalVisible, setEditModalVisible] = useState(false);
  const [editForm] = Form.useForm();

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

  const handleEdit = () => {
    editForm.setFieldsValue({
      serverName: server.serverName,
      baseUrl: server.baseUrl,
      prontoDebtorCode: server.prontoDebtorCode,
      prontoXiVersion: server.prontoXiVersion,
      apiKey: '', // Don't pre-fill API key for security
    });
    setEditModalVisible(true);
  };

  const handleEditSubmit = async (values) => {
    try {
      // Only include apiKey if it was provided
      const updateData = {
        serverName: values.serverName,
        baseUrl: values.baseUrl,
        prontoDebtorCode: values.prontoDebtorCode,
        prontoXiVersion: values.prontoXiVersion,
      };

      if (values.apiKey && values.apiKey.trim() !== '') {
        updateData.apiKey = values.apiKey;
      }

      await serverService.updateServer(id, updateData);
      message.success('Server updated successfully');
      setEditModalVisible(false);
      editForm.resetFields();
      loadServer();
    } catch (error) {
      message.error('Failed to update server');
    }
  };

  const handleEditCancel = () => {
    setEditModalVisible(false);
    editForm.resetFields();
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
        <Button icon={<EditOutlined />} type="primary" onClick={handleEdit}>
          Edit
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

      <Modal
        title="Edit Server"
        open={editModalVisible}
        onCancel={handleEditCancel}
        onOk={() => editForm.submit()}
        okText="Save"
        width={600}
      >
        <Form
          form={editForm}
          layout="vertical"
          onFinish={handleEditSubmit}
        >
          <Form.Item
            label="Server Name"
            name="serverName"
            rules={[{ required: true, message: 'Please enter server name' }]}
          >
            <Input placeholder="Enter server name" />
          </Form.Item>

          <Form.Item
            label="Base URL"
            name="baseUrl"
            rules={[
              { required: true, message: 'Please enter base URL' },
              { type: 'url', message: 'Please enter a valid URL' }
            ]}
          >
            <Input placeholder="https://cognos.example.com" />
          </Form.Item>

          <Form.Item
            label="API Key (leave blank to keep existing)"
            name="apiKey"
          >
            <Input.Password placeholder="Enter new API key (optional)" />
          </Form.Item>

          <Form.Item
            label="Pronto Debtor Code"
            name="prontoDebtorCode"
            rules={[{ required: true, message: 'Please enter debtor code' }]}
          >
            <Input placeholder="Enter debtor code" />
          </Form.Item>

          <Form.Item
            label="Pronto Xi Version"
            name="prontoXiVersion"
            rules={[{ required: true, message: 'Please enter Xi version' }]}
          >
            <Input placeholder="Enter Xi version" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default ServerDetail;
