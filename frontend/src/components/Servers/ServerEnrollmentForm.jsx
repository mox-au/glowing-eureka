import React, { useState } from 'react';
import { Form, Input, Button, Card, message, Space } from 'antd';
import { useNavigate } from 'react-router-dom';
import serverService from '../../services/serverService';

const ServerEnrollmentForm = () => {
  const [loading, setLoading] = useState(false);
  const [form] = Form.useForm();
  const navigate = useNavigate();

  const onFinish = async (values) => {
    setLoading(true);
    try {
      await serverService.enrollServer(values);
      message.success('Server enrolled successfully');
      navigate('/servers');
    } catch (error) {
      message.error(error.response?.data?.error || 'Failed to enroll server');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <h1>Enroll New Server</h1>
      <Card style={{ maxWidth: 800 }}>
        <Form
          form={form}
          layout="vertical"
          onFinish={onFinish}
        >
          <Form.Item
            label="Server Name"
            name="serverName"
            rules={[{ required: true, message: 'Please enter server name' }]}
          >
            <Input placeholder="e.g., Production Server 1" />
          </Form.Item>

          <Form.Item
            label="Base URL"
            name="baseUrl"
            rules={[
              { required: true, message: 'Please enter base URL' },
              { type: 'url', message: 'Please enter a valid URL' },
            ]}
          >
            <Input placeholder="https://cognos.client1.com" />
          </Form.Item>

          <Form.Item
            label="API Key"
            name="apiKey"
            rules={[{ required: true, message: 'Please enter API key' }]}
          >
            <Input.Password placeholder="Enter Cognos API key" />
          </Form.Item>

          <Form.Item
            label="Pronto Debtor Code"
            name="prontoDebtorCode"
            rules={[{ required: true, message: 'Please enter debtor code' }]}
          >
            <Input placeholder="e.g., DC001" />
          </Form.Item>

          <Form.Item
            label="Pronto Xi Version"
            name="prontoXiVersion"
            rules={[{ required: true, message: 'Please enter Xi version' }]}
          >
            <Input placeholder="e.g., 11.2.5" />
          </Form.Item>

          <Form.Item>
            <Space>
              <Button type="primary" htmlType="submit" loading={loading}>
                Enroll Server
              </Button>
              <Button onClick={() => navigate('/servers')}>
                Cancel
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
};

export default ServerEnrollmentForm;
