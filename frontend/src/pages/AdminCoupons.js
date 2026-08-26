import React, { useEffect, useState } from "react";
import api from "../api/axios";

const empty = { code: "", description: "", discountPercent: "", maxDiscountAmount: "", minOrderAmount: "", usageLimit: "" };

export default function AdminCoupons() {
  const [coupons, setCoupons] = useState([]);
  const [form, setForm] = useState(empty);

  useEffect(() => {
    load();
  }, []);

  const load = () => api.get("/admin/coupons").then((res) => setCoupons(res.data));

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    await api.post("/admin/coupons", {
      ...form,
      discountPercent: Number(form.discountPercent),
      maxDiscountAmount: form.maxDiscountAmount ? Number(form.maxDiscountAmount) : null,
      minOrderAmount: form.minOrderAmount ? Number(form.minOrderAmount) : null,
      usageLimit: form.usageLimit ? Number(form.usageLimit) : null,
    });
    setForm(empty);
    load();
  };

  const deactivate = async (id) => {
    await api.patch(`/admin/coupons/${id}/deactivate`);
    load();
  };

  return (
    <div className="container mt-4">
      <h3>Manage Coupons</h3>
      <form className="row g-2 mb-4" onSubmit={handleSubmit}>
        <div className="col-md-2">
          <input name="code" className="form-control" placeholder="CODE" value={form.code} onChange={handleChange} required />
        </div>
        <div className="col-md-3">
          <input name="description" className="form-control" placeholder="Description" value={form.description} onChange={handleChange} />
        </div>
        <div className="col-md-2">
          <input name="discountPercent" type="number" className="form-control" placeholder="Discount %" value={form.discountPercent} onChange={handleChange} required />
        </div>
        <div className="col-md-2">
          <input name="maxDiscountAmount" type="number" className="form-control" placeholder="Max ₹" value={form.maxDiscountAmount} onChange={handleChange} />
        </div>
        <div className="col-md-2">
          <input name="minOrderAmount" type="number" className="form-control" placeholder="Min order ₹" value={form.minOrderAmount} onChange={handleChange} />
        </div>
        <div className="col-md-1">
          <button className="btn btn-success w-100" type="submit">Add</button>
        </div>
      </form>

      <table className="table">
        <thead><tr><th>Code</th><th>Discount</th><th>Used</th><th>Active</th><th></th></tr></thead>
        <tbody>
          {coupons.map((c) => (
            <tr key={c.id}>
              <td>{c.code}</td>
              <td>{c.discountPercent}%</td>
              <td>{c.usedCount}{c.usageLimit ? ` / ${c.usageLimit}` : ""}</td>
              <td>{c.active ? "Yes" : "No"}</td>
              <td>{c.active && <button className="btn btn-sm btn-outline-danger" onClick={() => deactivate(c.id)}>Deactivate</button>}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
