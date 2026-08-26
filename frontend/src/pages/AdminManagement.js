import React, { useEffect, useState } from "react";
import api from "../api/axios";

export default function AdminManagement() {
  const [tab, setTab] = useState("sellers");
  const [sellers, setSellers] = useState([]);
  const [customers, setCustomers] = useState([]);
  const [orders, setOrders] = useState([]);
  const [categories, setCategories] = useState([]);
  const [newCategory, setNewCategory] = useState({ name: "", description: "" });

  useEffect(() => {
    api.get("/admin/management/sellers").then((res) => setSellers(res.data));
    api.get("/admin/management/customers").then((res) => setCustomers(res.data));
    api.get("/admin/management/orders").then((res) => setOrders(res.data));
    api.get("/admin/management/categories").then((res) => setCategories(res.data));
  }, []);

  const toggleEnabled = async (id) => {
    await api.patch(`/admin/management/users/${id}/toggle-enabled`);
    api.get("/admin/management/sellers").then((res) => setSellers(res.data));
    api.get("/admin/management/customers").then((res) => setCustomers(res.data));
  };

  const addCategory = async (e) => {
    e.preventDefault();
    await api.post("/admin/management/categories", newCategory);
    setNewCategory({ name: "", description: "" });
    api.get("/admin/management/categories").then((res) => setCategories(res.data));
  };

  const deleteCategory = async (id) => {
    await api.delete(`/admin/management/categories/${id}`);
    api.get("/admin/management/categories").then((res) => setCategories(res.data));
  };

  return (
    <div className="container mt-4">
      <h3>Manage Platform</h3>
      <ul className="nav nav-tabs mb-3">
        {["sellers", "customers", "orders", "categories"].map((t) => (
          <li className="nav-item" key={t}>
            <button className={`nav-link ${tab === t ? "active" : ""}`} onClick={() => setTab(t)}>
              {t.charAt(0).toUpperCase() + t.slice(1)}
            </button>
          </li>
        ))}
      </ul>

      {tab === "sellers" && (
        <table className="table">
          <thead><tr><th>Name</th><th>Email</th><th>Status</th><th></th></tr></thead>
          <tbody>
            {sellers.map((s) => (
              <tr key={s.id}>
                <td>{s.name}</td><td>{s.email}</td>
                <td>{s.enabled ? "Active" : "Disabled"}</td>
                <td><button className="btn btn-sm btn-outline-secondary" onClick={() => toggleEnabled(s.id)}>Toggle</button></td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      {tab === "customers" && (
        <table className="table">
          <thead><tr><th>Name</th><th>Email</th><th>Status</th><th></th></tr></thead>
          <tbody>
            {customers.map((c) => (
              <tr key={c.id}>
                <td>{c.name}</td><td>{c.email}</td>
                <td>{c.enabled ? "Active" : "Disabled"}</td>
                <td><button className="btn btn-sm btn-outline-secondary" onClick={() => toggleEnabled(c.id)}>Toggle</button></td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      {tab === "orders" && (
        <table className="table">
          <thead><tr><th>#</th><th>Customer</th><th>Seller</th><th>Total</th><th>Status</th></tr></thead>
          <tbody>
            {orders.map((o) => (
              <tr key={o.id}>
                <td>{o.id}</td><td>{o.customer?.name}</td><td>{o.seller?.name}</td>
                <td>₹{o.totalAmount}</td><td>{o.status}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      {tab === "categories" && (
        <>
          <form className="row g-2 mb-3" onSubmit={addCategory}>
            <div className="col-md-4">
              <input className="form-control" placeholder="Category name" value={newCategory.name}
                onChange={(e) => setNewCategory({ ...newCategory, name: e.target.value })} required />
            </div>
            <div className="col-md-5">
              <input className="form-control" placeholder="Description" value={newCategory.description}
                onChange={(e) => setNewCategory({ ...newCategory, description: e.target.value })} />
            </div>
            <div className="col-md-3">
              <button className="btn btn-success w-100" type="submit">Add Category</button>
            </div>
          </form>
          <table className="table">
            <thead><tr><th>Name</th><th>Description</th><th></th></tr></thead>
            <tbody>
              {categories.map((c) => (
                <tr key={c.id}>
                  <td>{c.name}</td><td>{c.description}</td>
                  <td><button className="btn btn-sm btn-outline-danger" onClick={() => deleteCategory(c.id)}>Delete</button></td>
                </tr>
              ))}
            </tbody>
          </table>
        </>
      )}
    </div>
  );
}
