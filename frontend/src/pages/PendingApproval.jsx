import React from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { logout } from '../store/slices/authSlice';
import styles from './PendingApproval.module.css';

export default function PendingApproval() {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { username } = useSelector(s => s.auth);

  return (
    <div className={styles.page}>
      <div className={styles.card}>
        <div className={styles.icon}>⏳</div>
        <h1>Account Pending Approval</h1>
        <p>Hello <strong>{username}</strong>, your retailer account has been submitted for review.</p>
        <p>An admin will verify your business details and GST ID before granting access.</p>
        <div className={styles.steps}>
          <div className={styles.step}><span className={styles.done}>✓</span> Registration Submitted</div>
          <div className={styles.step}><span className={styles.pending}>⏳</span> Admin Verification</div>
          <div className={styles.step}><span className={styles.waiting}>○</span> Account Activated</div>
        </div>
        <p className={styles.note}>You will be able to browse the catalog and place orders once approved.</p>
        <button className={styles.btn} onClick={() => { dispatch(logout()); navigate('/login'); }}>
          Back to Login
        </button>
      </div>
    </div>
  );
}
