import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ConfigProvider } from 'antd';
import { AuthProvider, useAuth } from './context/AuthContext';
import LoginForm from './components/Auth/LoginForm';
import AppLayout from './components/Layout/AppLayout';
import DashboardOverview from './components/Dashboard/DashboardOverview';
import ServerList from './components/Servers/ServerList';
import ServerEnrollmentForm from './components/Servers/ServerEnrollmentForm';
import ServerDetail from './components/Servers/ServerDetail';

const PrivateRoute = ({ children }) => {
  const { isAuthenticated, loading } = useAuth();

  if (loading) {
    return <div>Loading...</div>;
  }

  return isAuthenticated ? children : <Navigate to="/login" />;
};

const App = () => {
  return (
    <ConfigProvider
      theme={{
        token: {
          colorPrimary: '#1890ff',
        },
      }}
    >
      <AuthProvider>
        <BrowserRouter>
          <Routes>
            <Route path="/login" element={<LoginForm />} />
            <Route
              path="/"
              element={
                <PrivateRoute>
                  <AppLayout />
                </PrivateRoute>
              }
            >
              <Route index element={<Navigate to="/dashboard" />} />
              <Route path="dashboard" element={<DashboardOverview />} />
              <Route path="servers" element={<ServerList />} />
              <Route path="servers/enroll" element={<ServerEnrollmentForm />} />
              <Route path="servers/:id" element={<ServerDetail />} />
              <Route path="bulk-operations" element={<div>Bulk Operations (Coming Soon)</div>} />
              <Route path="reports" element={<div>Reports (Coming Soon)</div>} />
              <Route path="users" element={<div>User Management (Coming Soon)</div>} />
              <Route path="audit" element={<div>Audit Logs (Coming Soon)</div>} />
            </Route>
          </Routes>
        </BrowserRouter>
      </AuthProvider>
    </ConfigProvider>
  );
};

export default App;
