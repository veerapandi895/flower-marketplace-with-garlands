import React, { useEffect, useState } from "react";
import api from "../api/axios";
import ImageUploader from "../components/ImageUploader";

export default function SellerShop() {
  const [form, setForm] = useState({
    shopName: "", logoUrl: "", bannerUrl: "", description: "",
    address: "", phone: "", email: "", gstNumber: "", deliveryRadiusKm: "", businessHours: "",
  });
  const [exists, setExists] = useState(false);
  const [message, setMessage] = useState("");

  useEffect(() => {
    api.get("/seller/shop").then((res) => {
      setForm(res.data);
      setExists(true);
    }).catch(() => {});
  }, []);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage("");
    try {
      if (exists) {
        await api.put("/seller/shop", form);
        setMessage("Shop updated successfully!");
      } else {
        await api.post("/seller/shop", form);
        setMessage("Shop created successfully!");
        setExists(true);
      }
    } catch (err) {
      setMessage(err.response?.data?.message || "Something went wrong.");
    }
  };

  return (
    <div className="container mt-4" style={{ maxWidth: "600px" }}>
      <h3>{exists ? "Update Shop" : "Create Shop"}</h3>
      {message && <div className="alert alert-info">{message}</div>}
      <form onSubmit={handleSubmit}>
        <div className="mb-3">
          <label className="form-label">Shop Name</label>
          <input name="shopName" className="form-control" value={form.shopName || ""} onChange={handleChange} required />
        </div>
        <div className="mb-3">
          <label className="form-label">Shop Logo</label>
          <ImageUploader
            images={form.logoUrl ? [form.logoUrl] : []}
            onChange={(urls) => setForm({ ...form, logoUrl: urls[0] || "" })}
            uploadEndpoint="/seller/uploads/shop-logo"
            multiple={false}
            fieldName="file"
          />
        </div>
        <div className="mb-3">
          <label className="form-label">Shop Banner</label>
          <ImageUploader
            images={form.bannerUrl ? [form.bannerUrl] : []}
            onChange={(urls) => setForm({ ...form, bannerUrl: urls[0] || "" })}
            uploadEndpoint="/seller/uploads/shop-banner"
            multiple={false}
            fieldName="file"
          />
        </div>
        <div className="mb-3">
          <label className="form-label">Description</label>
          <textarea name="description" className="form-control" value={form.description || ""} onChange={handleChange} />
        </div>
        <div className="mb-3">
          <label className="form-label">Address</label>
          <input name="address" className="form-control" value={form.address || ""} onChange={handleChange} />
        </div>
        <div className="row">
          <div className="col-md-6 mb-3">
            <label className="form-label">Contact Phone</label>
            <input name="phone" className="form-control" value={form.phone || ""} onChange={handleChange} />
          </div>
          <div className="col-md-6 mb-3">
            <label className="form-label">Contact Email</label>
            <input name="email" className="form-control" value={form.email || ""} onChange={handleChange} />
          </div>
        </div>
        <div className="row">
          <div className="col-md-6 mb-3">
            <label className="form-label">GST Number (optional)</label>
            <input name="gstNumber" className="form-control" value={form.gstNumber || ""} onChange={handleChange} />
          </div>
          <div className="col-md-6 mb-3">
            <label className="form-label">Delivery Radius (km)</label>
            <input name="deliveryRadiusKm" type="number" className="form-control" value={form.deliveryRadiusKm || ""} onChange={handleChange} />
          </div>
        </div>
        <div className="mb-3">
          <label className="form-label">Business Hours</label>
          <input name="businessHours" className="form-control" placeholder="9:00 AM - 8:00 PM" value={form.businessHours || ""} onChange={handleChange} />
        </div>
        <button type="submit" className="btn btn-success w-100">
          {exists ? "Update Shop" : "Create Shop"}
        </button>
      </form>
    </div>
  );
}
