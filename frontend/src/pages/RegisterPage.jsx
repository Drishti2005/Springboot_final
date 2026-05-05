import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../api/axiosConfig';
import styles from './Auth.module.css';

export default function RegisterPage() {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    username: '', email: '', password: '', role: 'RETAILER', gstId: ''
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async e => {
    e.preventDefault();
    setLoading(true); setError('');
    try {
      await api.post('/api/auth/register', form);
      setSuccess('Registration successful! ' +
        (form.role === 'RETAILER' ? 'Please wait for admin approval before logging in.' : 'You can now log in.'));
      setTimeout(() => navigate('/login'), 3000);
    } catch (err) {
      setError(err.response?.data?.message || 'Registration failed');
    } finally { setLoading(false); }
  };

  return (
    <div className={styles.page}>
      <div className={styles.card} style={{ maxWidth: 480 }}>
        <div className={styles.header}>
          <div className={styles.icon}>📋</div>
          <h1>Business Registration</h1>
          <p>Create your wholesale account</p>
        </div>
        {error && <div className={styles.error}>{error}</div>}
        {success && <div className={styles.success}>{success}</div>}
        <form onSubmit={handleSubmit} className={styles.form}>
          <div className={styles.row}>
            <div className={styles.field}>
              <label>Username</label>
              <input type="text" placeholder="Username" required
                value={form.username} onChange={e => setForm({ ...form, username: e.target.value })} />
            </div>
            <div className={styles.field}>
              <label>Account Type</label>
              <select value={form.role} onChange={e => setForm({ ...form, role: e.target.value })}>
                <option value="RETAILER">Retailer (Buyer)</option>
                <option value="WHOLESALER">Wholesaler (Seller)</option>
                <option value="ADMIN">Admin</option>
              </select>
            </div>
          </div>
          <div className={styles.field}>
            <label>Business Email</label>
            <input type="email" placeholder="business@company.com" required
              value={form.email} onChange={e => setForm({ ...form, email: e.target.value })} />
          </div>
          <div className={styles.field}>
            <label>GST / Tax ID</label>
            <input type="text" placeholder="e.g. 27AAPFU0939F1ZV" required
              value={form.gstId} onChange={e => setForm({ ...form, gstId: e.target.value })} />
          </div>
          <div className={styles.field}>
            <label>Password</label>
            <input type="password" placeholder="Min 8 characters" required minLength={8}
              value={form.password} onChange={e => setForm({ ...form, password: e.target.value })} />
          </div>
          <button type="submit" className={styles.btn} disabled={loading}>
            {loading ? 'Registering...' : 'Create Account'}
          </button>
        </form>
        <p className={styles.footer}>
          Already have an account? <Link to="/login">Sign in</Link>
        </p>
      </div>
    </div>
  );
}
