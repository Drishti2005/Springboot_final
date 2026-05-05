import React from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { logout } from '../store/slices/authSlice';
import { clearCart } from '../store/slices/cartSlice';
import styles from './Navbar.module.css';

export default function Navbar({ title, cartCount }) {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { username, role } = useSelector(s => s.auth);

  const handleLogout = () => {
    dispatch(logout());
    dispatch(clearCart());
    navigate('/login');
  };

  return (
    <nav className={styles.nav}>
      <div className={styles.brand}>
        <span className={styles.logo}>🏭</span>
        <span className={styles.title}>{title || 'B2B Wholesale Portal'}</span>
      </div>
      <div className={styles.right}>
        {cartCount !== undefined && (
          <span className={styles.cartBadge}>🛒 {cartCount} items</span>
        )}
        <span className={styles.user}>
          <span className={styles.roleBadge} data-role={role}>{role}</span>
          {username}
        </span>
        <button className={styles.logoutBtn} onClick={handleLogout}>Logout</button>
      </div>
    </nav>
  );
}
