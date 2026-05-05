import { createSlice } from '@reduxjs/toolkit';

const cartSlice = createSlice({
  name: 'cart',
  initialState: { items: [] },
  reducers: {
    setItem(state, { payload }) {
      const idx = state.items.findIndex(i => i.productId === payload.productId);
      if (idx >= 0) state.items[idx] = payload;
      else state.items.push(payload);
    },
    removeItem(state, { payload }) {
      state.items = state.items.filter(i => i.productId !== payload);
    },
    setCart(state, { payload }) {
      state.items = payload;
    },
    clearCart(state) {
      state.items = [];
    },
  },
});

export const { setItem, removeItem, setCart, clearCart } = cartSlice.actions;
export default cartSlice.reducer;

export const selectTotal = state =>
  state.cart.items.reduce((s, i) => s + parseFloat(i.lineTotal || 0), 0);
