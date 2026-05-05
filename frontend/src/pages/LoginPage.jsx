import React, { useState } from 'react';
import { useDispatch } from 'react-redux';
import { useNavigate, Link } from 'react-router-dom';
import { loginSuccess } from '../store/slices/authSlice';
import api from '../api/axiosConfig';
import styles from './Auth.module.css';

export default function LoginPage() {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const [form, setForm] = useState({ username: '', password: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async e => {
    e.preventDefault();
    setLoading(true); setError('');
    try {
      const { data } = await api.post('/api/auth/login', form);
      dispatch(loginSuccess(data));
      if (data.role === 'ADMIN') navigate('/admin');
      else if (data.role === 'WHOLESALER') navigate('/wholesaler');
      else if (!data.approved) navigate('/pending');
      else navigate('/dashboard');
    } catch (err) {
      setError(err.response?.data?.message || 'Invalid username or password');
    } finally { setLoading(false); }
  };

  return (
    <div className={styles.page}>
      <div className={styles.card}>
        <div className={styles.header}>
          <div className={styles.icon}>🏭</div>
          <h1>B2B Wholesale Portal</h1>
          <p>Sign in to your account</p>
        </div>
        {error && <div className={styles.error}>{error}</div>}
        <form onSubmit={handleSubmit} className={styles.form}>
          <div className={styles.field}>
            <label>Username</label>
            <input
              type="text" placeholder="Enter username" required
              value={form.username}
              onChange={e => setForm({ ...form, username: e.target.value })}
            />
          </div>
          <div className={styles.field}>
            <label>Password</label>
            <input
              type="password" placeholder="Enter password" required
              value={form.password}
              onChange={e => setForm({ ...form, password: e.target.value })}
            />
          </div>
          <button type="submit" className={styles.btn} disabled={loading}>
            {loading ? 'Signing in...' : 'Sign In'}
          </button>
        </form>
        <p className={styles.footer}>
          New retailer? <Link to="/register">Register your business</Link>
        </p>
        <div className={styles.demoBox}>
          <p><strong>Demo Accounts</strong></p>
          <p>Register an Admin account first, then approve retailers</p>
        </div>
      </div>
    </div>
  );
}
