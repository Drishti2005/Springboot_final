import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { useSelector } from 'react-redux';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import RetailerDashboard from './pages/RetailerDashboard';
import AdminDashboard from './pages/AdminDashboard';
import WholesalerDashboard from './pages/WholesalerDashboard';
import PendingApproval from './pages/PendingApproval';

function PrivateRoute({ children, allowedRoles }) {
  const { token, role, approved } = useSelector(s => s.auth);
  if (!token) return <Navigate to="/login" replace />;
  if (allowedRoles && !allowedRoles.includes(role)) return <Navigate to="/login" replace />;
  if (role === 'RETAILER' && !approved && !window.location.pathname.includes('pending'))
    return <Navigate to="/pending" replace />;
  return children;
}

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/pending" element={<PendingApproval />} />
        <Route path="/dashboard" element={
          <PrivateRoute allowedRoles={['RETAILER']}>
            <RetailerDashboard />
          </PrivateRoute>
        } />
        <Route path="/admin" element={
          <PrivateRoute allowedRoles={['ADMIN']}>
            <AdminDashboard />
          </PrivateRoute>
        } />
        <Route path="/wholesaler" element={
          <PrivateRoute allowedRoles={['WHOLESALER']}>
            <WholesalerDashboard />
          </PrivateRoute>
        } />
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </BrowserRouter>
  );
}
