import { createSlice } from '@reduxjs/toolkit';

const stored = {
  token: localStorage.getItem('token'),
  username: localStorage.getItem('username'),
  role: localStorage.getItem('role'),
  approved: localStorage.getItem('approved') === 'true',
};

const authSlice = createSlice({
  name: 'auth',
  initialState: stored,
  reducers: {
    loginSuccess(state, { payload }) {
      Object.assign(state, payload);
      localStorage.setItem('token', payload.token);
      localStorage.setItem('username', payload.username);
      localStorage.setItem('role', payload.role);
      localStorage.setItem('approved', payload.approved);
    },
    logout(state) {
      state.token = null; state.username = null; state.role = null; state.approved = false;
      localStorage.clear();
    },
  },
});

export const { loginSuccess, logout } = authSlice.actions;
export default authSlice.reducer;
