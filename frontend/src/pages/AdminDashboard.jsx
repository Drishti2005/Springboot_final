import React, { useEffect, useState } from 'react';
import Navbar from '../components/Navbar';
import api from '../api/axiosConfig';
import styles from './AdminDashboard.module.css';

export default function AdminDashboard() {
  const [tab, setTab] = useState('pending');
  const [pending, setPending] = useState([]);
  const [products, setProducts] = useState([]);
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(false);
  const [toast, setToast] = useState('');
  const [newProduct, setNewProduct] = useState({
    name: '', sku: '', description: '', basePrice: '', moq: 10, stockQuantity: 100
  });
  const [tiers, setTiers] = useState([
    { minQuantity: 50, unitPrice: '', tierLabel: 'Tier 1' },
    { minQuantity: 100, unitPrice: '', tierLabel: 'Tier 2' },
  ]);

  const showToast = msg => { setToast(msg); setTimeout(() => setToast(''), 3000); };

  const loadPending = async () => {
    const { data } = await api.get('/api/admin/pending-retailers');
    setPending(data);
  };
  const loadProducts = async () => {
    const { data } = await api.get('/api/products');
    setProducts(data);
  };

  useEffect(() => {
    loadPending();
    loadProducts();
  }, []);

  const approve = async id => {
    await api.patch(`/api/admin/approve/${id}`);
    showToast('✅ Retailer approved successfully!');
    loadPending();
  };

  const addProduct = async e => {
    e.preventDefault();
    setLoading(true);
    try {
      const payload = {
        ...newProduct,
        basePrice: parseFloat(newProduct.basePrice),
        moq: parseInt(newProduct.moq),
        stockQuantity: parseInt(newProduct.stockQuantity),
        tieredPrices: tiers
          .filter(t => t.unitPrice)
          .map(t => ({ ...t, unitPrice: parseFloat(t.unitPrice) }))
      };
      await api.post('/api/products', payload);
      showToast('✅ Product added successfully!');
      setNewProduct({ name: '', sku: '', description: '', basePrice: '', moq: 10, stockQuantity: 100 });
      setTiers([
        { minQuantity: 50, unitPrice: '', tierLabel: 'Tier 1' },
        { minQuantity: 100, unitPrice: '', tierLabel: 'Tier 2' },
      ]);
      loadProducts();
    } catch (err) {
      showToast('❌ ' + (err.response?.data?.message || 'Failed to add product'));
    } finally { setLoading(false); }
  };

  return (
    <div className={styles.page}>
      <Navbar title="Admin Dashboard" />
      {toast && <div className={styles.toast}>{toast}</div>}

      <div className={styles.container}>
        {/* Stats */}
        <div className={styles.stats}>
          <div className={styles.stat}>
            <span className={styles.statNum}>{pending.length}</span>
            <span className={styles.statLabel}>Pending Approvals</span>
          </div>
          <div className={styles.stat}>
            <span className={styles.statNum}>{products.length}</span>
            <span className={styles.statLabel}>Total Products</span>
          </div>
          <div className={styles.stat}>
            <span className={styles.statNum}>
              {products.reduce((s, p) => s + p.stockQuantity, 0).toLocaleString()}
            </span>
            <span className={styles.statLabel}>Total Stock Units</span>
          </div>
        </div>

        {/* Tabs */}
        <div className={styles.tabs}>
          {['pending', 'products', 'addProduct'].map(t => (
            <button key={t} className={`${styles.tab} ${tab === t ? styles.active : ''}`}
              onClick={() => setTab(t)}>
              {t === 'pending' ? `⏳ Pending Approvals (${pending.length})`
                : t === 'products' ? '📦 Product Catalog'
                : '➕ Add Product'}
            </button>
          ))}
        </div>

        {/* Pending Approvals */}
        {tab === 'pending' && (
          <div className={styles.section}>
            {pending.length === 0 ? (
              <div className={styles.empty}>🎉 No pending approvals</div>
            ) : (
              <table className={styles.table}>
                <thead>
                  <tr><th>Username</th><th>Email</th><th>GST ID</th><th>Action</th></tr>
                </thead>
                <tbody>
                  {pending.map(u => (
                    <tr key={u.id}>
                      <td><strong>{u.username}</strong></td>
                      <td>{u.email}</td>
                      <td><code>{u.gstId}</code></td>
                      <td>
                        <button className={styles.approveBtn} onClick={() => approve(u.id)}>
                          ✓ Approve
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        )}

        {/* Product Catalog */}
        {tab === 'products' && (
          <div className={styles.section}>
            <table className={styles.table}>
              <thead>
                <tr><th>SKU</th><th>Name</th><th>Base Price</th><th>MOQ</th><th>Stock</th><th>Tiers</th></tr>
              </thead>
              <tbody>
                {products.map(p => (
                  <tr key={p.id}>
                    <td><code>{p.sku}</code></td>
                    <td>{p.name}</td>
                    <td>₹{parseFloat(p.basePrice).toFixed(2)}</td>
                    <td>{p.moq}</td>
                    <td>
                      <span className={p.stockQuantity < 50 ? styles.lowStock : styles.inStock}>
                        {p.stockQuantity}
                      </span>
                    </td>
                    <td>
                      {p.tieredPrices?.map(t => (
                        <span key={t.id} className={styles.tierChip}>
                          {t.tierLabel}: ₹{t.unitPrice} @{t.minQuantity}+
                        </span>
                      ))}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {/* Add Product */}
        {tab === 'addProduct' && (
          <div className={styles.section}>
            <form onSubmit={addProduct} className={styles.productForm}>
              <h3>Add New Product</h3>
              <div className={styles.formGrid}>
                <div className={styles.field}>
                  <label>Product Name *</label>
                  <input required placeholder="e.g. Industrial Bolt Set"
                    value={newProduct.name}
                    onChange={e => setNewProduct({ ...newProduct, name: e.target.value })} />
                </div>
                <div className={styles.field}>
                  <label>SKU *</label>
                  <input required placeholder="e.g. BOLT-M8-100"
                    value={newProduct.sku}
                    onChange={e => setNewProduct({ ...newProduct, sku: e.target.value })} />
                </div>
                <div className={styles.field}>
                  <label>Base Price (₹) *</label>
                  <input required type="number" step="0.01" placeholder="12.00"
                    value={newProduct.basePrice}
                    onChange={e => setNewProduct({ ...newProduct, basePrice: e.target.value })} />
                </div>
                <div className={styles.field}>
                  <label>MOQ (Min Order Qty) *</label>
                  <input required type="number" min="1"
                    value={newProduct.moq}
                    onChange={e => setNewProduct({ ...newProduct, moq: e.target.value })} />
                </div>
                <div className={styles.field}>
                  <label>Stock Quantity *</label>
                  <input required type="number" min="0"
                    value={newProduct.stockQuantity}
                    onChange={e => setNewProduct({ ...newProduct, stockQuantity: e.target.value })} />
                </div>
                <div className={styles.field}>
                  <label>Description</label>
                  <input placeholder="Optional description"
                    value={newProduct.description}
                    onChange={e => setNewProduct({ ...newProduct, description: e.target.value })} />
                </div>
              </div>

              <h4 style={{ marginTop: 20, marginBottom: 12 }}>Tiered Pricing Rules</h4>
              <div className={styles.tiersGrid}>
                {tiers.map((tier, i) => (
                  <div key={i} className={styles.tierRow}>
                    <span className={styles.tierLabel}>{tier.tierLabel}</span>
                    <div className={styles.field}>
                      <label>Min Qty</label>
                      <input type="number" value={tier.minQuantity}
                        onChange={e => {
                          const t = [...tiers]; t[i].minQuantity = parseInt(e.target.value); setTiers(t);
                        }} />
                    </div>
                    <div className={styles.field}>
                      <label>Unit Price (₹)</label>
                      <input type="number" step="0.01" placeholder="Leave blank to skip"
                        value={tier.unitPrice}
                        onChange={e => {
                          const t = [...tiers]; t[i].unitPrice = e.target.value; setTiers(t);
                        }} />
                    </div>
                  </div>
                ))}
              </div>

              <button type="submit" className={styles.submitBtn} disabled={loading}>
                {loading ? 'Adding...' : '➕ Add Product'}
              </button>
            </form>
          </div>
        )}
      </div>
    </div>
  );
}
