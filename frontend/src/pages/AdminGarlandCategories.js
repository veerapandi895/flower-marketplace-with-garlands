import React, { useEffect, useState } from "react";
import api from "../api/axios";

const empty = { name: "", description: "", imageUrl: "" };

export default function AdminGarlandCategories() {
  const [categories, setCategories] = useState([]);
  const [form, setForm] = useState(empty);
  const [editingId, setEditingId] = useState(null);

  useEffect(() => {
    load();
  }, []);

  const load = () => api.get("/admin/garland-categories").then((res) => setCategories(res.data));

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const resetForm = () => {
    setForm(empty);
    setEditingId(null);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingId) {
        await api.put(`/admin/garland-categories/${editingId}`, form);
      } else {
        await api.post("/admin/garland-categories", form);
      }
      resetForm();
      load();
    } catch (err) {
      alert(err.response?.data?.message || "Could not save category");
    }
  };

  const handleEdit = (category) => {
    setEditingId(category.id);
    setForm({ name: category.name, description: category.description || "", imageUrl: category.imageUrl || "" });
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Delete this garland category?")) return;
    await api.delete(`/admin/garland-categories/${id}`);
    load();
  };

  return (
    <div className="container mt-4">
      <h3>Garland Categories</h3>
      <p className="text-muted">e.g. Wedding Garland, Temple Garland, Rose Garland, Jasmine Garland, Reception Garland, VIP Garland, Bride Garland, Groom Garland, Custom Garland.</p>
      <form className="row g-2 mb-4" onSubmit={handleSubmit}>
        <div className="col-md-4">
          <input name="name" className="form-control" placeholder="Category name" value={form.name} onChange={handleChange} required />
        </div>
        <div className="col-md-4">
          <input name="description" className="form-control" placeholder="Description" value={form.description} onChange={handleChange} />
        </div>
        <div className="col-md-3">
          <input name="imageUrl" className="form-control" placeholder="Image URL" value={form.imageUrl} onChange={handleChange} />
        </div>
        <div className="col-md-1">
          <button type="submit" className="btn btn-success w-100">{editingId ? "Save" : "Add"}</button>
        </div>
      </form>

      <table className="table align-middle">
        <thead><tr><th>Name</th><th>Description</th><th>Actions</th></tr></thead>
        <tbody>
          {categories.map((c) => (
            <tr key={c.id}>
              <td>{c.name}</td>
              <td>{c.description}</td>
              <td className="d-flex gap-1">
                <button className="btn btn-sm btn-outline-primary" onClick={() => handleEdit(c)}>Edit</button>
                <button className="btn btn-sm btn-outline-danger" onClick={() => handleDelete(c.id)}>Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
