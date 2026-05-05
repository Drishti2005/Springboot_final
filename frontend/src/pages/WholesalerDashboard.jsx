import React, { useEffect, useState } from 'react';
import Navbar from '../components/Navbar';
import api from '../api/axiosConfig';
import styles from './WholesalerDashboard.module.css';

export default function WholesalerDashboard() {
  const [tab, setTab] = useState('products');
  const [products, setProducts] = useState([]);
  const [toast, setToast] = useState({ msg: '', type: 'success' });
  const [newProduct, setNewProduct] = useState({
    name: '', sku: '', description: '', basePrice: '', moq: 10, stockQuantity: 100
  });
  const [tiers, setTiers] = useState([
    { minQuantity: 50, unitPrice: '', tierLabel: 'Tier 1' },
    { minQuantity: 100, unitPrice: '', tierLabel: 'Tier 2' },
  ]);
  const [loading, setLoading] = useState(false);

  const showToast = (msg, type = 'success') => {
    setToast({ msg, type });
    setTimeout(() => setToast({ msg: '', type: 'success' }), 3000);
  };

  const loadProducts = async () => {
    const { data } = await api.get('/api/products');
    setProducts(data);
  };

  useEffect(() => { loadProducts(); }, []);

  const addProduct = async e => {
    e.preventDefault();
    setLoading(true);
    try {
      const payload = {
        ...newProduct,
        basePrice: parseFloat(newProduct.basePrice),
        moq: parseInt(newProduct.moq),
        stockQuantity: parseInt(newProduct.stockQuantity),
        tieredPrices: tiers.filter(t => t.unitPrice).map(t => ({ ...t, unitPrice: parseFloat(t.unitPrice) }))
      };
      await api.post('/api/products', payload);
      showToast('✅ Product added!');
      setNewProduct({ name: '', sku: '', description: '', basePrice: '', moq: 10, stockQuantity: 100 });
      setTiers([
        { minQuantity: 50, unitPrice: '', tierLabel: 'Tier 1' },
        { minQuantity: 100, unitPrice: '', tierLabel: 'Tier 2' },
      ]);
      loadProducts();
      setTab('products');
    } catch (err) {
      showToast('❌ ' + (err.response?.data?.message || 'Failed'), 'error');
    } finally { setLoading(false); }
  };

  const totalStock = products.reduce((s, p) => s + p.stockQuantity, 0);

  return (
    <div className={styles.page}>
      <Navbar title="Wholesaler Dashboard" />
      {toast.msg && <div className={`${styles.toast} ${styles[toast.type]}`}>{toast.msg}</div>}

      <div className={styles.container}>
        <div className={styles.stats}>
          <div className={styles.stat}><span className={styles.num}>{products.length}</span><span className={styles.lbl}>Products</span></div>
          <div className={styles.stat}><span className={styles.num}>{totalStock.toLocaleString()}</span><span className={styles.lbl}>Total Stock</span></div>
          <div className={styles.stat}><span className={styles.num}>{products.filter(p => p.stockQuantity < 50).length}</span><span className={styles.lbl}>Low Stock</span></div>
        </div>

        <div className={styles.tabs}>
          {['products', 'addProduct'].map(t => (
            <button key={t} className={`${styles.tab} ${tab === t ? styles.active : ''}`} onClick={() => setTab(t)}>
              {t === 'products' ? '📦 My Products' : '➕ Add Product'}
            </button>
          ))}
        </div>

        {tab === 'products' && (
          <div className={styles.section}>
            <div className={styles.grid}>
              {products.map(p => (
                <div key={p.id} className={styles.card}>
                  <div className={styles.cardTop}>
                    <div>
                      <h3>{p.name}</h3>
                      <code>{p.sku}</code>
                    </div>
                    <div className={styles.price}>₹{parseFloat(p.basePrice).toFixed(2)}</div>
                  </div>
                  <div className={styles.cardMeta}>
                    <span className={styles.moq}>MOQ: {p.moq}</span>
                    <span className={p.stockQuantity < 50 ? styles.low : styles.ok}>
                      Stock: {p.stockQuantity}
                    </span>
                  </div>
                  {p.tieredPrices?.length > 0 && (
                    <div className={styles.tierList}>
                      {p.tieredPrices.map(t => (
                        <span key={t.id} className={styles.tier}>
                          {t.tierLabel}: ₹{t.unitPrice} @ {t.minQuantity}+
                        </span>
                      ))}
                    </div>
                  )}
                </div>
              ))}
            </div>
          </div>
        )}

        {tab === 'addProduct' && (
          <div className={styles.section}>
            <form onSubmit={addProduct} className={styles.form}>
              <h3>Add New Product to Catalog</h3>
              <div className={styles.formGrid}>
                <div className={styles.field}><label>Product Name *</label>
                  <input required placeholder="e.g. Steel Pipe 2 inch" value={newProduct.name}
                    onChange={e => setNewProduct({ ...newProduct, name: e.target.value })} /></div>
                <div className={styles.field}><label>SKU *</label>
                  <input required placeholder="e.g. PIPE-2IN-SS" value={newProduct.sku}
                    onChange={e => setNewProduct({ ...newProduct, sku: e.target.value })} /></div>
                <div className={styles.field}><label>Base Price (₹) *</label>
                  <input required type="number" step="0.01" placeholder="0.00" value={newProduct.basePrice}
                    onChange={e => setNewProduct({ ...newProduct, basePrice: e.target.value })} /></div>
                <div className={styles.field}><label>MOQ *</label>
                  <input required type="number" min="1" value={newProduct.moq}
                    onChange={e => setNewProduct({ ...newProduct, moq: e.target.value })} /></div>
                <div className={styles.field}><label>Stock Quantity *</label>
                  <input required type="number" min="0" value={newProduct.stockQuantity}
                    onChange={e => setNewProduct({ ...newProduct, stockQuantity: e.target.value })} /></div>
                <div className={styles.field}><label>Description</label>
                  <input placeholder="Optional" value={newProduct.description}
                    onChange={e => setNewProduct({ ...newProduct, description: e.target.value })} /></div>
              </div>
              <h4 style={{ margin: '20px 0 12px' }}>Tiered Pricing</h4>
              {tiers.map((tier, i) => (
                <div key={i} className={styles.tierRow}>
                  <span className={styles.tierName}>{tier.tierLabel}</span>
                  <div className={styles.field}><label>Min Qty</label>
                    <input type="number" value={tier.minQuantity}
                      onChange={e => { const t = [...tiers]; t[i].minQuantity = parseInt(e.target.value); setTiers(t); }} /></div>
                  <div className={styles.field}><label>Unit Price (₹)</label>
                    <input type="number" step="0.01" placeholder="Leave blank to skip" value={tier.unitPrice}
                      onChange={e => { const t = [...tiers]; t[i].unitPrice = e.target.value; setTiers(t); }} /></div>
                </div>
              ))}
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
