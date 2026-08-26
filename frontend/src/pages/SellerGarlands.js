import React, { useEffect, useState } from "react";
import api from "../api/axios";
import ImageUploader from "../components/ImageUploader";

const emptyForm = {
  name: "", description: "", categoryId: "", images: [],
  price: "", weightGrams: "", availableQuantity: "",
  flowerComposition: "", estimatedPrepMinutes: "",
};

export default function SellerGarlands() {
  const [garlands, setGarlands] = useState([]);
  const [categories, setCategories] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [editingId, setEditingId] = useState(null);

  useEffect(() => {
    loadGarlands();
    api.get("/public/garlands/categories").then((res) => setCategories(res.data));
  }, []);

  const loadGarlands = () => {
    api.get("/seller/garlands").then((res) => setGarlands(res.data));
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
      price: Number(form.price),
      weightGrams: form.weightGrams ? Number(form.weightGrams) : null,
      availableQuantity: Number(form.availableQuantity),
      estimatedPrepMinutes: form.estimatedPrepMinutes ? Number(form.estimatedPrepMinutes) : null,
    };
    try {
      if (editingId) {
        await api.put(`/seller/garlands/${editingId}`, payload);
      } else {
        await api.post("/seller/garlands", payload);
      }
      resetForm();
      loadGarlands();
    } catch (err) {
      alert(err.response?.data?.message || "Could not save garland");
    }
  };

  const handleEdit = (garland) => {
    setEditingId(garland.id);
    setForm({
      name: garland.name,
      description: garland.description || "",
      categoryId: garland.categoryId || "",
      images: garland.images || [],
      price: garland.price,
      weightGrams: garland.weightGrams || "",
      availableQuantity: garland.availableQuantity,
      flowerComposition: garland.flowerComposition || "",
      estimatedPrepMinutes: garland.estimatedPrepMinutes || "",
    });
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Delete this garland listing?")) return;
    await api.delete(`/seller/garlands/${id}`);
    loadGarlands();
  };

  const toggleActive = async (garland) => {
    await api.patch(`/seller/garlands/${garland.id}/active`, null, {
      params: { value: garland.status === "INACTIVE" },
    });
    loadGarlands();
  };

  const updateStock = async (garland, value) => {
    await api.patch(`/seller/garlands/${garland.id}/stock`, null, { params: { value } });
    loadGarlands();
  };

  return (
    <div className="container mt-4">
      <h3>{editingId ? "Update Garland" : "Add Garland"}</h3>
      <form className="row g-2 mb-4" onSubmit={handleSubmit}>
        <div className="col-md-4">
          <input name="name" className="form-control" placeholder="Garland name" value={form.name} onChange={handleChange} required />
        </div>
        <div className="col-md-4">
          <select name="categoryId" className="form-select" value={form.categoryId} onChange={handleChange}>
            <option value="">Category</option>
            {categories.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}
          </select>
        </div>
        <div className="col-md-4">
          <label className="form-label small">Garland Images</label>
          <ImageUploader
            images={form.images}
            onChange={(images) => setForm({ ...form, images })}
            uploadEndpoint="/seller/uploads/garland-images"
            multiple
          />
        </div>
        <div className="col-12">
          <textarea name="description" className="form-control" placeholder="Description" value={form.description} onChange={handleChange} />
        </div>
        <div className="col-md-3">
          <input name="price" type="number" step="0.01" className="form-control" placeholder="Price (₹)" value={form.price} onChange={handleChange} required />
        </div>
        <div className="col-md-3">
          <input name="weightGrams" type="number" step="0.01" className="form-control" placeholder="Weight (g)" value={form.weightGrams} onChange={handleChange} />
        </div>
        <div className="col-md-3">
          <input name="availableQuantity" type="number" className="form-control" placeholder="Available quantity" value={form.availableQuantity} onChange={handleChange} required />
        </div>
        <div className="col-md-3">
          <input name="estimatedPrepMinutes" type="number" className="form-control" placeholder="Prep time (mins)" value={form.estimatedPrepMinutes} onChange={handleChange} />
        </div>
        <div className="col-12">
          <input name="flowerComposition" className="form-control" placeholder="Flower composition (e.g. Rose + Jasmine + Marigold)" value={form.flowerComposition} onChange={handleChange} />
        </div>
        <div className="col-12 d-flex gap-2">
          <button type="submit" className="btn btn-success">{editingId ? "Update" : "Add Garland"}</button>
          {editingId && <button type="button" className="btn btn-outline-secondary" onClick={resetForm}>Cancel</button>}
        </div>
      </form>

      <h4>My Garlands</h4>
      <div className="table-responsive">
        <table className="table align-middle">
          <thead>
            <tr>
              <th>Name</th><th>Price</th><th>Stock</th><th>Status</th><th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {garlands.map((g) => (
              <tr key={g.id}>
                <td>{g.name}</td>
                <td>₹{g.currentPrice ?? g.price} <small className="text-muted">(base ₹{g.price})</small></td>
                <td style={{ width: "140px" }}>
                  <input
                    type="number"
                    className="form-control form-control-sm"
                    defaultValue={g.availableQuantity}
                    onBlur={(e) => updateStock(g, Number(e.target.value))}
                  />
                </td>
                <td>
                  {g.status === "ACTIVE" && <span className="badge bg-success">Active</span>}
                  {g.status === "OUT_OF_STOCK" && <span className="badge bg-danger">Out of Stock</span>}
                  {g.status === "INACTIVE" && <span className="badge bg-secondary">Inactive</span>}
                </td>
                <td className="d-flex gap-1 flex-wrap">
                  <button className="btn btn-sm btn-outline-primary" onClick={() => handleEdit(g)}>Edit</button>
                  <button className="btn btn-sm btn-outline-danger" onClick={() => handleDelete(g.id)}>Delete</button>
                  <button className="btn btn-sm btn-outline-warning" onClick={() => toggleActive(g)}>
                    {g.status === "INACTIVE" ? "Activate" : "Deactivate"}
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
