import React, { useEffect, useState, useRef, useCallback } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import Navbar from '../components/Navbar';
import api from '../api/axiosConfig';
import { setItem, removeItem, setCart, clearCart, selectTotal } from '../store/slices/cartSlice';
import styles from './RetailerDashboard.module.css';

export default function RetailerDashboard() {
  const dispatch = useDispatch();
  const cartItems = useSelector(s => s.cart.items);
  const cartTotal = useSelector(selectTotal);

  const [tab, setTab] = useState('catalog');
  const [products, setProducts] = useState([]);
  const [orders, setOrders] = useState([]);
  const [profile, setProfile] = useState(null);
  const [quantities, setQuantities] = useState({});
  const [toast, setToast] = useState({ msg: '', type: 'success' });
  const [checkoutLoading, setCheckoutLoading] = useState(false);
  const [csvLoading, setCsvLoading] = useState(false);
  const [csvResult, setCsvResult] = useState(null);
  const [pricingLoading, setPricingLoading] = useState({});
  const [paymentTerm, setPaymentTerm] = useState('CREDIT');
  const fileRef = useRef();
  const debounceTimers = useRef({});

  const showToast = (msg, type = 'success') => {
    setToast({ msg, type });
    setTimeout(() => setToast({ msg: '', type: 'success' }), 4000);
  };

  const loadData = useCallback(async () => {
    const [prodRes, profileRes] = await Promise.all([
      api.get('/api/products'),
      api.get('/api/orders').catch(() => ({ data: [] })),
    ]);
    setProducts(prodRes.data);
    setOrders(profileRes.data);
    const init = {};
    prodRes.data.forEach(p => { init[p.id] = p.moq; });
    setQuantities(init);
  }, []);

  useEffect(() => { loadData(); }, [loadData]);

  // Real-time tiered pricing
  const fetchPrice = useCallback(async (productId, qty) => {
    setPricingLoading(prev => ({ ...prev, [productId]: true }));
    try {
      const { data } = await api.get('/api/pricing/quote', { params: { productId, quantity: qty } });
      dispatch(setItem({
        productId, sku: data.sku, name: products.find(p => p.id === productId)?.name || '',
        quantity: qty, unitPrice: data.unitPrice, lineTotal: data.lineTotal, appliedTier: data.appliedTier,
      }));
    } catch (err) {
      showToast('⚠️ ' + (err.response?.data?.message || 'Pricing error'), 'error');
    } finally {
      setPricingLoading(prev => ({ ...prev, [productId]: false }));
    }
  }, [dispatch, products]);

  const handleQtyChange = (productId, val) => {
    const qty = parseInt(val) || 1;
    setQuantities(prev => ({ ...prev, [productId]: qty }));
    // Update cart in real-time if item is already in cart
    if (cartItems.find(i => i.productId === productId)) {
      clearTimeout(debounceTimers.current[productId]);
      debounceTimers.current[productId] = setTimeout(() => fetchPrice(productId, qty), 500);
    }
  };

  const addToCart = async (product) => {
    const qty = quantities[product.id] || product.moq;
    await fetchPrice(product.id, qty);
    showToast(`✅ ${product.name} added to cart`);
  };

  const handleCheckout = async () => {
    if (cartItems.length === 0) return showToast('Cart is empty', 'error');
    setCheckoutLoading(true);
    try {
      const payload = { items: cartItems.map(i => ({ productId: i.productId, quantity: i.quantity })) };
      const { data } = await api.post('/api/orders/checkout', payload);
      dispatch(clearCart());
      showToast(`🎉 Order #${data.id} placed! Invoice generated.`);
      setTab('orders');
      loadData();
    } catch (err) {
      showToast('❌ ' + (err.response?.data?.message || 'Checkout failed'), 'error');
    } finally { setCheckoutLoading(false); }
  };

  const handleQuickReorder = async () => {
    try {
      const { data } = await api.get('/api/orders/last');
      const items = data.items.map(i => ({
        productId: i.product.id, sku: i.product.sku, name: i.product.name,
        quantity: i.quantity, unitPrice: i.unitPrice,
        lineTotal: (i.unitPrice * i.quantity).toFixed(2), appliedTier: i.appliedTier || 'Base',
      }));
      dispatch(setCart(items));
      setTab('cart');
      showToast(`🔄 Last order loaded into cart (${items.length} items)`);
    } catch {
      showToast('No previous orders found', 'error');
    }
  };

  const handleCsvUpload = async e => {
    const file = e.target.files[0];
    if (!file) return;
    setCsvLoading(true); setCsvResult(null);
    const formData = new FormData();
    formData.append('file', file);
    try {
      const { data } = await api.post('/api/bulk/upload', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      });
      setCsvResult(data);
      if (data.orderId) {
        showToast(`✅ Bulk order #${data.orderId} placed! ${data.successCount} items processed.`);
        setTab('orders');
        loadData();
      }
    } catch (err) {
      showToast('❌ CSV upload failed', 'error');
    } finally { setCsvLoading(false); fileRef.current.value = ''; }
  };

  const downloadInvoice = async (orderId) => {
    try {
      const res = await api.get(`/api/orders/${orderId}/invoice`, { responseType: 'blob' });
      const url = URL.createObjectURL(new Blob([res.data], { type: 'application/pdf' }));
      const a = document.createElement('a'); a.href = url; a.download = `invoice-${orderId}.pdf`; a.click();
    } catch { showToast('Invoice not available yet', 'error'); }
  };

  const gst = cartTotal * 0.18;
  const grandTotal = cartTotal + gst;

  return (
    <div className={styles.page}>
      <Navbar title="Retailer Portal" cartCount={cartItems.length} />
      {toast.msg && <div className={`${styles.toast} ${styles[toast.type]}`}>{toast.msg}</div>}

      <div className={styles.container}>
        {/* Quick Actions Bar */}
        <div className={styles.quickBar}>
          <button className={styles.reorderBtn} onClick={handleQuickReorder}>
            🔄 Quick Re-order
          </button>
          <label className={styles.csvBtn}>
            {csvLoading ? '⏳ Processing...' : '📤 Bulk CSV Upload'}
            <input ref={fileRef} type="file" accept=".csv" hidden onChange={handleCsvUpload} disabled={csvLoading} />
          </label>
          <a href="/bulk-template.csv" download className={styles.templateLink}>
            ⬇ Download CSV Template
          </a>
        </div>

        {/* CSV Result */}
        {csvResult && (
          <div className={styles.csvResult}>
            <strong>CSV Upload Result:</strong> {csvResult.successCount} items added,{' '}
            {csvResult.failureCount} failed.
            {csvResult.errors?.length > 0 && (
              <ul>{csvResult.errors.map((e, i) => <li key={i}>{e}</li>)}</ul>
            )}
          </div>
        )}

        <div className={styles.layout}>
          {/* Left: Catalog / Orders */}
          <div className={styles.main}>
            <div className={styles.tabs}>
              {['catalog', 'orders'].map(t => (
                <button key={t} className={`${styles.tab} ${tab === t ? styles.active : ''}`}
                  onClick={() => setTab(t)}>
                  {t === 'catalog' ? '🛍 Product Catalog' : `📋 My Orders (${orders.length})`}
                </button>
              ))}
            </div>

            {/* Catalog */}
            {tab === 'catalog' && (
              <div className={styles.catalog}>
                {products.map(product => {
                  const inCart = cartItems.find(i => i.productId === product.id);
                  const qty = quantities[product.id] || product.moq;
                  return (
                    <div key={product.id} className={`${styles.productCard} ${inCart ? styles.inCartCard : ''}`}>
                      <div className={styles.productHeader}>
                        <div>
                          <h3>{product.name}</h3>
                          <code className={styles.sku}>{product.sku}</code>
                        </div>
                        <div className={styles.priceBlock}>
                          <span className={styles.basePrice}>₹{parseFloat(product.basePrice).toFixed(2)}</span>
                          <span className={styles.perUnit}>/ unit</span>
                        </div>
                      </div>
                      {product.description && <p className={styles.desc}>{product.description}</p>}
                      <div className={styles.badges}>
                        <span className={styles.moqBadge}>MOQ: {product.moq}</span>
                        <span className={product.stockQuantity > 50 ? styles.inStock : styles.lowStock}>
                          Stock: {product.stockQuantity}
                        </span>
                      </div>
                      {/* Tier pricing display */}
                      {product.tieredPrices?.length > 0 && (
                        <div className={styles.tiers}>
                          {product.tieredPrices.map(t => (
                            <span key={t.id} className={`${styles.tierBadge} ${qty >= t.minQuantity ? styles.activeTier : ''}`}>
                              {t.tierLabel}: ₹{t.unitPrice} @ {t.minQuantity}+
                            </span>
                          ))}
                        </div>
                      )}
                      <div className={styles.addRow}>
                        <div className={styles.qtyControl}>
                          <button onClick={() => handleQtyChange(product.id, Math.max(product.moq, qty - 10))}>−</button>
                          <input type="number" value={qty} min={product.moq}
                            onChange={e => handleQtyChange(product.id, e.target.value)} />
                          <button onClick={() => handleQtyChange(product.id, qty + 10)}>+</button>
                        </div>
                        <button
                          className={`${styles.addBtn} ${inCart ? styles.updateBtn : ''}`}
                          onClick={() => addToCart(product)}
                          disabled={pricingLoading[product.id] || product.stockQuantity === 0}
                        >
                          {pricingLoading[product.id] ? '...' : inCart ? '🔄 Update' : '🛒 Add to Cart'}
                        </button>
                      </div>
                      {inCart && (
                        <div className={styles.cartPreview}>
                          In cart: {inCart.quantity} × ₹{parseFloat(inCart.unitPrice).toFixed(2)} = <strong>₹{parseFloat(inCart.lineTotal).toFixed(2)}</strong>
                          <span className={styles.tierApplied}>{inCart.appliedTier}</span>
                        </div>
                      )}
                    </div>
                  );
                })}
              </div>
            )}

            {/* Orders */}
            {tab === 'orders' && (
              <div className={styles.ordersSection}>
                {orders.length === 0 ? (
                  <div className={styles.empty}>No orders yet. Start shopping!</div>
                ) : (
                  orders.map(order => (
                    <div key={order.id} className={styles.orderCard}>
                      <div className={styles.orderHeader}>
                        <div>
                          <strong>Order #{order.id}</strong>
                          <span className={`${styles.status} ${styles[order.status?.toLowerCase()]}`}>
                            {order.status}
                          </span>
                        </div>
                        <div className={styles.orderMeta}>
                          <span>{new Date(order.orderDate).toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' })}</span>
                          <strong>₹{parseFloat(order.totalAmount || 0).toFixed(2)}</strong>
                        </div>
                      </div>
                      <div className={styles.orderItems}>
                        {order.items?.map(item => (
                          <div key={item.id} className={styles.orderItem}>
                            <span>{item.product?.name}</span>
                            <span>{item.quantity} × ₹{parseFloat(item.unitPrice).toFixed(2)}</span>
                            <span className={styles.tierApplied}>{item.appliedTier}</span>
                          </div>
                        ))}
                      </div>
                      <button className={styles.invoiceBtn} onClick={() => downloadInvoice(order.id)}>
                        📄 Download Invoice
                      </button>
                    </div>
                  ))
                )}
              </div>
            )}
          </div>

          {/* Right: Cart */}
          <div className={styles.cartPanel}>
            <div className={styles.cartHeader}>
              <h2>🛒 Cart ({cartItems.length})</h2>
              {cartItems.length > 0 && (
                <button className={styles.clearBtn} onClick={() => dispatch(clearCart())}>Clear</button>
              )}
            </div>

            {cartItems.length === 0 ? (
              <div className={styles.emptyCart}>
                <div style={{ fontSize: 48 }}>🛒</div>
                <p>Your cart is empty</p>
                <p style={{ fontSize: 12, color: '#aaa' }}>Add products from the catalog</p>
              </div>
            ) : (
              <>
                <div className={styles.cartItems}>
                  {cartItems.map(item => (
                    <div key={item.productId} className={styles.cartItem}>
                      <div className={styles.cartItemInfo}>
                        <strong>{item.name}</strong>
                        <code>{item.sku}</code>
                        <span className={styles.tierApplied}>{item.appliedTier}</span>
                      </div>
                      <div className={styles.cartItemPrice}>
                        <span>{item.quantity} × ₹{parseFloat(item.unitPrice).toFixed(2)}</span>
                        <strong>₹{parseFloat(item.lineTotal).toFixed(2)}</strong>
                        <button className={styles.removeBtn}
                          onClick={() => dispatch(removeItem(item.productId))}>✕</button>
                      </div>
                    </div>
                  ))}
                </div>

                <div className={styles.cartSummary}>
                  <div className={styles.summaryRow}><span>Subtotal</span><span>₹{cartTotal.toFixed(2)}</span></div>
                  <div className={styles.summaryRow}><span>GST (18%)</span><span>₹{gst.toFixed(2)}</span></div>
                  <div className={`${styles.summaryRow} ${styles.totalRow}`}>
                    <span>Total</span><span>₹{grandTotal.toFixed(2)}</span>
                  </div>
                </div>

                <div className={styles.paymentTerm}>
                  <label>Payment Term</label>
                  <div className={styles.termOptions}>
                    <label className={`${styles.termOpt} ${paymentTerm === 'CREDIT' ? styles.termActive : ''}`}>
                      <input type="radio" value="CREDIT" checked={paymentTerm === 'CREDIT'}
                        onChange={() => setPaymentTerm('CREDIT')} />
                      💳 Credit / Net 30
                    </label>
                    <label className={`${styles.termOpt} ${paymentTerm === 'PAY_NOW' ? styles.termActive : ''}`}>
                      <input type="radio" value="PAY_NOW" checked={paymentTerm === 'PAY_NOW'}
                        onChange={() => setPaymentTerm('PAY_NOW')} />
                      💰 Pay Now
                    </label>
                  </div>
                </div>

                <button className={styles.checkoutBtn} onClick={handleCheckout} disabled={checkoutLoading}>
                  {checkoutLoading ? '⏳ Processing...' : '✅ Place Order & Generate Invoice'}
                </button>
              </>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
