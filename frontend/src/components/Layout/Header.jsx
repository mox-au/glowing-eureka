import React from 'react';
import { Layout, Button, Dropdown, Space } from 'antd';
import { LogoutOutlined, UserOutlined, DownOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

const { Header: AntHeader } = Layout;

const Header = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  const items = [
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: 'Logout',
      onClick: handleLogout,
    },
  ];

  return (
    <AntHeader style={{
      background: '#fff',
      padding: '0 24px',
      display: 'flex',
      justifyContent: 'space-between',
      alignItems: 'center'
    }}>
      <div>
        <h2 style={{ margin: 0 }}>Cognos Analytics Enterprise Management</h2>
      </div>
      <Dropdown menu={{ items }} trigger={['click']}>
        <Button type="text">
          <Space>
            <UserOutlined />
            {user?.username} ({user?.role})
            <DownOutlined />
          </Space>
        </Button>
      </Dropdown>
    </AntHeader>
  );
};

export default Header;
