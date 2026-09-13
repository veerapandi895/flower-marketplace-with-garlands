import React, { useEffect, useState } from "react";
import api from "../api/axios";
import ImageUploader from "../components/ImageUploader";

const emptyForm = {
  name: "", description: "", categoryId: "", images: [],
  basePrice: "", quantity: "", unit: "PIECE", freshnessHours: 24,
  harvestDate: "", expiryDate: "",
};

export default function SellerFlowers() {
  const [flowers, setFlowers] = useState([]);
  const [categories, setCategories] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [editingId, setEditingId] = useState(null);
  const [suggestion, setSuggestion] = useState({});

  useEffect(() => {
    loadFlowers();
    api.get("/public/categories").then((res) => setCategories(res.data));
  }, []);

  const loadFlowers = () => {
    api.get("/seller/flowers").then((res) => setFlowers(res.data));
  };

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const resetForm = () => {
    setForm(emptyForm);
    setEditingId(null);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const payload = {
      ...form,
      categoryId: form.categoryId || null,
      images: form.images,
      basePrice: Number(form.basePrice),
      quantity: Number(form.quantity),
      freshnessHours: Number(form.freshnessHours),
    };
    try {
      if (editingId) {
        await api.put(`/seller/flowers/${editingId}`, payload);
      } else {
        await api.post("/seller/flowers", payload);
      }
      resetForm();
      loadFlowers();
    } catch (err) {
      alert(err.response?.data?.message || "Could not save flower");
    }
  };

  const handleEdit = (flower) => {
    setEditingId(flower.id);
    setForm({
      name: flower.name,
      description: flower.description || "",
      categoryId: "",
      images: flower.images || [],
      basePrice: flower.basePrice,
      quantity: flower.quantity,
      unit: flower.unit,
      freshnessHours: flower.freshnessHours,
      harvestDate: flower.harvestDate?.slice(0, 16) || "",
      expiryDate: flower.expiryDate?.slice(0, 16) || "",
    });
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Delete this flower listing?")) return;
    await api.delete(`/seller/flowers/${id}`);
    loadFlowers();
  };

  const toggleOutOfStock = async (flower) => {
    await api.patch(`/seller/flowers/${flower.id}/out-of-stock`, null, {
      params: { value: !flower.outOfStock },
    });
    loadFlowers();
  };

  const getSuggestion = async (id) => {
    const { data } = await api.get(`/seller/flowers/${id}/price-suggestion`);
    setSuggestion({ ...suggestion, [id]: data.suggestion });
  };

  const runQualityCheck = async (id) => {
    try {
      await api.post(`/seller/flowers/${id}/quality-check`);
      loadFlowers();
    } catch (err) {
      alert(err.response?.data?.message || "AI quality check failed");
    }
  };

  return (
    <div className="container mt-4">
      <h3>{editingId ? "Update Flower" : "Add Flower"}</h3>
      <form className="row g-2 mb-4" onSubmit={handleSubmit}>
        <div className="col-md-4">
          <input name="name" className="form-control" placeholder="Flower name" value={form.name} onChange={handleChange} required />
        </div>
        <div className="col-md-4">
          <select name="categoryId" className="form-select" value={form.categoryId} onChange={handleChange}>
            <option value="">Category</option>
            {categories.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}
          </select>
        </div>
        <div className="col-md-4">
          <label className="form-label small">Flower Images</label>
          <ImageUploader
            images={form.images}
            onChange={(images) => setForm({ ...form, images })}
            uploadEndpoint="/cloudinary/upload"
            multiple
          />
        </div>
        <div className="col-12">
          <textarea name="description" className="form-control" placeholder="Description" value={form.description} onChange={handleChange} />
        </div>
        <div className="col-md-3">
          <input name="basePrice" type="number" step="0.01" className="form-control" placeholder="Base price (₹)" value={form.basePrice} onChange={handleChange} required />
        </div>
        <div className="col-md-3">
          <input name="quantity" type="number" step="0.01" className="form-control" placeholder="Quantity" value={form.quantity} onChange={handleChange} required />
        </div>
        <div className="col-md-3">
          <select name="unit" className="form-select" value={form.unit} onChange={handleChange}>
            <option value="PIECE">Piece</option>
            <option value="BUNCH">Bunch</option>
            <option value="KG">Kg</option>
          </select>
        </div>
        <div className="col-md-3">
          <input name="freshnessHours" type="number" className="form-control" placeholder="Freshness (hours)" value={form.freshnessHours} onChange={handleChange} required />
        </div>
        <div className="col-md-6">
          <label className="form-label small">Harvest Date</label>
          <input name="harvestDate" type="datetime-local" className="form-control" value={form.harvestDate} onChange={handleChange} required />
        </div>
        <div className="col-md-6">
          <label className="form-label small">Expiry Date</label>
          <input name="expiryDate" type="datetime-local" className="form-control" value={form.expiryDate} onChange={handleChange} />
        </div>
        <div className="col-12 d-flex gap-2">
          <button type="submit" className="btn btn-success">{editingId ? "Update" : "Add Flower"}</button>
          {editingId && <button type="button" className="btn btn-outline-secondary" onClick={resetForm}>Cancel</button>}
        </div>
      </form>

      <h4>My Flowers</h4>
      <div className="table-responsive">
        <table className="table align-middle">
          <thead>
            <tr>
              <th>Name</th><th>Price</th><th>Stock</th><th>Stage</th><th>Status</th><th>AI Quality</th><th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {flowers.map((f) => (
              <tr key={f.id}>
                <td>{f.name}</td>
                <td>₹{f.currentPrice} <small className="text-muted">(base ₹{f.basePrice})</small></td>
                <td>{f.quantity} {f.unit}</td>
                <td><span className="badge bg-info">{f.priceStage}</span></td>
                <td>{f.outOfStock ? <span className="badge bg-danger">Out of Stock</span> : <span className="badge bg-success">Available</span>}</td>
                <td>
                  {f.qualityScore != null ? (
                    <span className={`badge ${f.qualityScore >= 70 ? "bg-success" : f.qualityScore >= 40 ? "bg-warning text-dark" : "bg-danger"}`} title={f.qualityVerdict}>
                      {f.qualityScore}/100
                    </span>
                  ) : (
                    <span className="text-muted small">Not checked</span>
                  )}
                </td>
                <td className="d-flex gap-1 flex-wrap">
                  <button className="btn btn-sm btn-outline-primary" onClick={() => handleEdit(f)}>Edit</button>
                  <button className="btn btn-sm btn-outline-danger" onClick={() => handleDelete(f.id)}>Delete</button>
                  <button className="btn btn-sm btn-outline-warning" onClick={() => toggleOutOfStock(f)}>
                    {f.outOfStock ? "Mark In Stock" : "Mark Out of Stock"}
                  </button>
                  <button className="btn btn-sm btn-outline-info" onClick={() => getSuggestion(f.id)}>
                    Price Tip
                  </button>
                  <button className="btn btn-sm btn-outline-success" onClick={() => runQualityCheck(f.id)}>
                    AI Quality Check
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      {flowers.some((f) => f.qualityVerdict) && (
        <div className="mb-3">
          {flowers.filter((f) => f.qualityVerdict).map((f) => (
            <div key={f.id} className="alert alert-light border small py-1 px-2 mb-1">
              <strong>{f.name}:</strong> {f.qualityVerdict}
            </div>
          ))}
        </div>
      )}
      {Object.entries(suggestion).map(([id, text]) => (
        <div key={id} className="alert alert-secondary">
          {flowers.find((f) => f.id === Number(id))?.name}: {text}
        </div>
      ))}
    </div>
  );
}
