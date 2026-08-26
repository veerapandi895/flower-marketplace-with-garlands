import React, { useEffect, useState } from "react";
import api from "../api/axios";

const empty = { festivalType: "PONGAL", title: "", description: "", discountPercent: "", startDate: "", endDate: "" };

const FESTIVALS = ["VALENTINES_DAY", "MOTHERS_DAY", "TEMPLE_FESTIVAL", "WEDDING_SEASON", "DIWALI", "PONGAL", "OTHER"];

export default function AdminFestivalOffers() {
  const [offers, setOffers] = useState([]);
  const [form, setForm] = useState(empty);

  useEffect(() => {
    load();
  }, []);

  const load = () => api.get("/admin/festival-offers").then((res) => setOffers(res.data));

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    await api.post("/admin/festival-offers", { ...form, discountPercent: Number(form.discountPercent) });
    setForm(empty);
    load();
  };

  const deactivate = async (id) => {
    await api.patch(`/admin/festival-offers/${id}/deactivate`);
    load();
  };

  return (
    <div className="container mt-4">
      <h3>Manage Festival Offers</h3>
      <form className="row g-2 mb-4" onSubmit={handleSubmit}>
        <div className="col-md-2">
          <select name="festivalType" className="form-select" value={form.festivalType} onChange={handleChange}>
            {FESTIVALS.map((f) => <option key={f} value={f}>{f.replaceAll("_", " ")}</option>)}
          </select>
        </div>
        <div className="col-md-2">
          <input name="title" className="form-control" placeholder="Title" value={form.title} onChange={handleChange} required />
        </div>
        <div className="col-md-2">
          <input name="discountPercent" type="number" className="form-control" placeholder="Discount %" value={form.discountPercent} onChange={handleChange} required />
        </div>
        <div className="col-md-2">
          <input name="startDate" type="datetime-local" className="form-control" value={form.startDate} onChange={handleChange} required />
        </div>
        <div className="col-md-2">
          <input name="endDate" type="datetime-local" className="form-control" value={form.endDate} onChange={handleChange} required />
        </div>
        <div className="col-md-2">
          <button className="btn btn-success w-100" type="submit">Add Offer</button>
        </div>
      </form>

      <table className="table">
        <thead><tr><th>Title</th><th>Type</th><th>Discount</th><th>Active</th><th></th></tr></thead>
        <tbody>
          {offers.map((o) => (
            <tr key={o.id}>
              <td>{o.title}</td><td>{o.festivalType}</td><td>{o.discountPercent}%</td>
              <td>{o.active ? "Yes" : "No"}</td>
              <td>{o.active && <button className="btn btn-sm btn-outline-danger" onClick={() => deactivate(o.id)}>Deactivate</button>}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
